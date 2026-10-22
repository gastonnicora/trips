package com.gastonnicora.trips.unit.routePoint;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.mappers.RoutePointMapper;
import com.gastonnicora.trips.repositories.RoutePointRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RoutePointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoutePointTest {

    @InjectMocks
    private RoutePointService routePointService;

    @Mock
    private RoutePointRepository routePointRepository;
    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RoutePointMapper routePointMapper;

    @Test
    void shouldCreateRoutePointSuccessfully() {

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        RoutePointCreate request = new RoutePointCreate(
                "Terminal La Plata",
                -34.9214,
                -57.9545
        );

        RoutePointDTO expectedDTO = new RoutePointDTO(
                UUID.randomUUID(),
                "Terminal La Plata",
                -34.9214,
                -57.9545,
                null,
                null,
                true
        );

        when(routePointRepository.save(any(RoutePoint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(routePointMapper.toDTO(any(RoutePoint.class)))
                .thenReturn(expectedDTO);
        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));

        RoutePointDTO result = routePointService.createRoutePoint(company.getUuid(),route.getUuid(), request);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<RoutePoint> captor =
                ArgumentCaptor.forClass(RoutePoint.class);

        verify(routePointRepository).save(captor.capture());


        verify(routeRepository).findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(route.getUuid(), company.getUuid());

        RoutePoint savedPoint = captor.getValue();

        assertEquals("Terminal La Plata", savedPoint.getName());
        assertEquals(-34.9214, savedPoint.getLatitude());
        assertEquals(-57.9545, savedPoint.getLongitude());

        // Verifica la relación bidireccional con la ruta
        assertEquals(route, savedPoint.getRoute());
        assertTrue(route.getPoints().contains(savedPoint));

        // Verifica la conversión al DTO
        verify(routePointMapper).toDTO(savedPoint);
    }
}