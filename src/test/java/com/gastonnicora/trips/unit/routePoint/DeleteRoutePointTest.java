package com.gastonnicora.trips.unit.routePoint;

import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.exceptions.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteRoutePointTest {

    @InjectMocks
    private RoutePointService routePointService;

    @Mock
    private RoutePointRepository routePointRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RoutePointMapper routePointMapper;

    @Test
    void shouldDeactivateRoutePointSuccessfully() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID routePointUuid = UUID.randomUUID();

        RoutePoint routePoint = new RoutePoint("Terminal La Plata", -34.9214, -57.9545);
        routePoint.setUuid(routePointUuid);

        when(routePointRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                             companyUuid, routeUuid, routePointUuid)).thenReturn(Optional.of(routePoint));

        routePointService.deleteRoutePoint(companyUuid, routeUuid, routePointUuid);

        ArgumentCaptor<RoutePoint> captor = ArgumentCaptor.forClass(RoutePoint.class);
        verify(routePointRepository).save(captor.capture());

        RoutePoint saved = captor.getValue();
        assertSame(routePoint, saved);
        assertFalse(saved.isActive());
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenRoutePointDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID routePointUuid = UUID.randomUUID();

        when(routePointRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                             companyUuid, routeUuid, routePointUuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                                            () -> routePointService.deleteRoutePoint(companyUuid, routeUuid, routePointUuid));

        assertEquals("No existe el punto de ruta.", ex.getMessage());
        verify(routePointRepository, never()).save(any(RoutePoint.class));
    }
}