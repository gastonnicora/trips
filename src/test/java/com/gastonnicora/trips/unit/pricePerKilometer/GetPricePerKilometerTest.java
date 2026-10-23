package com.gastonnicora.trips.unit.pricePerKilometer;

import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.PricePerKilometerMapper;
import com.gastonnicora.trips.repositories.PricePerKilometerRepository;
import com.gastonnicora.trips.services.PricePerKilometerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class GetPricePerKilometerTest {

    @InjectMocks
    private PricePerKilometerService pricePerKilometerService;

    @Mock
    private PricePerKilometerRepository pricePerKilometerRepository;

    @Mock
    private PricePerKilometerMapper pricePerKilometerMapper;

    @Test
    void shouldReturnPricePerKilometerSuccessfully() {

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        PricePerKilometer price = new PricePerKilometer(
                route, BigDecimal.valueOf(100), BigDecimal.valueOf(10000), BigDecimal.valueOf(500));
        price.setUuid(UUID.randomUUID());

        PricePerKilometerDTO expectedDTO = new PricePerKilometerDTO(
                price.getUuid(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(500),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(pricePerKilometerRepository
                     .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             price.getUuid(), company.getUuid(), route.getUuid()))
                .thenReturn(Optional.of(price));
        when(pricePerKilometerMapper.toDTO(price)).thenReturn(expectedDTO);

        PricePerKilometerDTO result = pricePerKilometerService.getPricePerKilometer(
                company.getUuid(), route.getUuid(), price.getUuid());

        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(pricePerKilometerMapper).toDTO(price);
    }

    @Test
    void shouldThrowNotFoundWhenPricePerKilometerDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID priceUuid = UUID.randomUUID();

        when(pricePerKilometerRepository
                     .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             priceUuid, companyUuid, routeUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> pricePerKilometerService.getPricePerKilometer(companyUuid, routeUuid, priceUuid)
                    );

        verify(pricePerKilometerMapper, never()).toDTO(any(PricePerKilometer.class));
    }
}