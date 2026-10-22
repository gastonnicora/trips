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

    @Test
    void shouldCreatePricePerKilometerSuccessfully() {

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        PricePerKilometerCreate request = new PricePerKilometerCreate(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500)
        );

        PricePerKilometerDTO expectedDTO = new PricePerKilometerDTO(
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(
                route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));
        when(pricePerKilometerRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             company.getUuid(), route.getUuid())).thenReturn(Optional.empty());
        when(pricePerKilometerRepository.save(any(PricePerKilometer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pricePerKilometerMapper.toDTO(any(PricePerKilometer.class))).thenReturn(expectedDTO);

        PricePerKilometerDTO result = pricePerKilometerService.createPricePerKilometer(
                company.getUuid(), route.getUuid(), request);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<PricePerKilometer> captor = ArgumentCaptor.forClass(PricePerKilometer.class);
        verify(pricePerKilometerRepository).save(captor.capture());

        PricePerKilometer savedPrice = captor.getValue();

        assertEquals(BigDecimal.valueOf(100), savedPrice.getBaseKm());
        assertEquals(BigDecimal.valueOf(10000), savedPrice.getBasePrice());
        assertEquals(BigDecimal.valueOf(500), savedPrice.getPricePerKm());
        assertTrue(savedPrice.isActive());

        // Verifica la relación bidireccional con la ruta
        assertEquals(route, savedPrice.getRoute());
        assertSame(savedPrice, route.getPricePerKilometer());

        verify(routeRepository).save(route);
        verify(pricePerKilometerMapper).toDTO(savedPrice);
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenRouteDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        PricePerKilometerCreate request = new PricePerKilometerCreate(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500)
        );

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> pricePerKilometerService.createPricePerKilometer(companyUuid, routeUuid, request));

        verify(pricePerKilometerRepository, never()).save(any(PricePerKilometer.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowConflictAndNotSaveWhenRouteAlreadyHasPricePerKilometer() {

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        PricePerKilometer existing = new PricePerKilometer(
                route, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);

        PricePerKilometerCreate request = new PricePerKilometerCreate(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500)
        );

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(
                route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));
        when(pricePerKilometerRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             company.getUuid(), route.getUuid())).thenReturn(Optional.of(existing));

        assertThrows(
                ConflictException.class,
                () -> pricePerKilometerService.createPricePerKilometer(
                        company.getUuid(), route.getUuid(), request));

        verify(pricePerKilometerRepository, never()).save(any(PricePerKilometer.class));
        verify(routeRepository, never()).save(any(Route.class));
        verify(pricePerKilometerMapper, never()).toDTO(any(PricePerKilometer.class));
    }
}