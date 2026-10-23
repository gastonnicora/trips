package com.gastonnicora.trips.unit.pricePerKilometer;

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePricePerKilometerTest {

    @InjectMocks
    private PricePerKilometerService pricePerKilometerService;

    @Mock
    private PricePerKilometerRepository pricePerKilometerRepository;

    @Mock
    private PricePerKilometerMapper pricePerKilometerMapper;

    @Test
    void shouldDeactivatePricePerKilometerSuccessfully() {

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        PricePerKilometer price = new PricePerKilometer(
                route, BigDecimal.valueOf(100), BigDecimal.valueOf(10000), BigDecimal.valueOf(500));
        price.setUuid(UUID.randomUUID());

        when(pricePerKilometerRepository
                     .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             price.getUuid(), company.getUuid(), route.getUuid()))
                .thenReturn(Optional.of(price));

        pricePerKilometerService.deletePricePerKilometer(company.getUuid(), route.getUuid(), price.getUuid());

        ArgumentCaptor<PricePerKilometer> captor = ArgumentCaptor.forClass(PricePerKilometer.class);
        verify(pricePerKilometerRepository).save(captor.capture());

        PricePerKilometer savedPrice = captor.getValue();

        assertSame(price, savedPrice);
        assertFalse(savedPrice.isActive());
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenPricePerKilometerDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID priceUuid = UUID.randomUUID();

        when(pricePerKilometerRepository
                     .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             priceUuid, companyUuid, routeUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> pricePerKilometerService.deletePricePerKilometer(companyUuid, routeUuid, priceUuid)
                    );

        verify(pricePerKilometerRepository, never()).save(any(PricePerKilometer.class));
    }
}