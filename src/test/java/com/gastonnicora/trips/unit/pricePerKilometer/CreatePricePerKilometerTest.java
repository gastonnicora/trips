package com.gastonnicora.trips.unit.pricePerKilometer;

import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.dtos.request.pricePerKilometer.PricePerKilometerCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.PricePerKilometerMapper;
import com.gastonnicora.trips.repositories.PricePerKilometerRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.PricePerKilometerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePricePerKilometerTest {

    @InjectMocks
    private PricePerKilometerService pricePerKilometerService;

    @Mock
    private PricePerKilometerRepository pricePerKilometerRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private PricePerKilometerMapper pricePerKilometerMapper;

    private Company company;
    private Route route;

    private void givenRouteExists() {
        company = new Company();
        company.setUuid(UUID.randomUUID());

        route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(
                route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));
    }

    private PricePerKilometerCreate request() {
        return new PricePerKilometerCreate(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500)
        );
    }

    @Test
    void shouldCreatePricePerKilometerWhenRouteHasNoPrice() {

        givenRouteExists();

        PricePerKilometerDTO expectedDTO = new PricePerKilometerDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(pricePerKilometerRepository.save(any(PricePerKilometer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pricePerKilometerMapper.toDTO(any(PricePerKilometer.class))).thenReturn(expectedDTO);

        PricePerKilometerDTO result = pricePerKilometerService.createPricePerKilometer(
                company.getUuid(), route.getUuid(), request());

        assertEquals(expectedDTO, result);

        ArgumentCaptor<PricePerKilometer> captor = ArgumentCaptor.forClass(PricePerKilometer.class);
        verify(pricePerKilometerRepository).save(captor.capture());

        PricePerKilometer savedPrice = captor.getValue();

        assertEquals(BigDecimal.valueOf(100), savedPrice.getBaseKm());
        assertEquals(BigDecimal.valueOf(10000), savedPrice.getBasePrice());
        assertEquals(BigDecimal.valueOf(500), savedPrice.getPricePerKm());
        assertTrue(savedPrice.isActive());
        assertEquals(route, savedPrice.getRoute());
        assertSame(savedPrice, route.getPricePerKilometer());

        verify(routeRepository).save(route);
        verify(pricePerKilometerMapper).toDTO(savedPrice);
    }

    @Test
    void shouldReactivateInactivePricePerKilometerInsteadOfCreatingNewOne() {

        givenRouteExists();

        PricePerKilometer inactive = new PricePerKilometer(
                route, BigDecimal.valueOf(50), BigDecimal.valueOf(5000), BigDecimal.valueOf(300));
        inactive.setActive(false);
        route.setPricePerKilometer(inactive);

        when(pricePerKilometerRepository.save(any(PricePerKilometer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pricePerKilometerMapper.toDTO(any(PricePerKilometer.class))).thenReturn(null);

        pricePerKilometerService.createPricePerKilometer(company.getUuid(), route.getUuid(), request());

        ArgumentCaptor<PricePerKilometer> captor = ArgumentCaptor.forClass(PricePerKilometer.class);
        verify(pricePerKilometerRepository).save(captor.capture());

        PricePerKilometer savedPrice = captor.getValue();

        // Se reutiliza la misma instancia: no se crea un registro nuevo para la ruta
        assertSame(inactive, savedPrice);
        assertSame(inactive, route.getPricePerKilometer());
        assertTrue(savedPrice.isActive());
        assertEquals(BigDecimal.valueOf(100), savedPrice.getBaseKm());
        assertEquals(BigDecimal.valueOf(10000), savedPrice.getBasePrice());
        assertEquals(BigDecimal.valueOf(500), savedPrice.getPricePerKm());

        verify(routeRepository).save(route);
    }

    @Test
    void shouldThrowConflictAndNotSaveWhenRouteAlreadyHasActivePrice() {

        givenRouteExists();

        PricePerKilometer active = new PricePerKilometer(
                route, BigDecimal.valueOf(50), BigDecimal.valueOf(5000), BigDecimal.valueOf(300));
        route.setPricePerKilometer(active);

        assertThrows(
                ConflictException.class,
                () -> pricePerKilometerService.createPricePerKilometer(
                        company.getUuid(), route.getUuid(), request())
                    );

        verify(pricePerKilometerRepository, never()).save(any(PricePerKilometer.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenRouteDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> pricePerKilometerService.createPricePerKilometer(companyUuid, routeUuid, request())
                    );

        verify(pricePerKilometerRepository, never()).save(any(PricePerKilometer.class));
        verify(routeRepository, never()).save(any(Route.class));
    }
}