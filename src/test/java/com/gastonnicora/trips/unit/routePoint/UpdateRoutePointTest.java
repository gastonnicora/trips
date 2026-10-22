package com.gastonnicora.trips.unit.routePoint;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RoutePointMapper;
import com.gastonnicora.trips.repositories.RoutePointRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRoutePointTest {

    @InjectMocks
    private RoutePointService routePointService;

    @Mock
    private RoutePointRepository routePointRepository;


    @Mock
    private RoutePointMapper routePointMapper;

    @Test
    void shouldThrowNotFoundAndNotSaveWhenRoutePointDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID routePointUuid = UUID.randomUUID();

        RoutePointCreate request = new RoutePointCreate(
                "Terminal La Plata", -34.9214, -57.9545
        );

        when(routePointRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                             companyUuid, routeUuid, routePointUuid)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> routePointService.updateRoutePoint(companyUuid, routeUuid, routePointUuid, request)
                                           );

        assertEquals("No existe el punto de ruta.", ex.getMessage());
        verify(routePointRepository, never()).save(any(RoutePoint.class));
        verify(routePointMapper, never()).toDTO(any());
    }

    @Test
    void shouldUpdateRoutePointSuccessfully() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();
        UUID routePointUuid = UUID.randomUUID();

        RoutePoint routePoint = new RoutePoint("Viejo nombre", -34.0, -58.0);
        routePoint.setUuid(routePointUuid);

        RoutePointCreate request = new RoutePointCreate(
                "Terminal La Plata", -34.9214, -57.9545
        );

        RoutePointDTO expectedDTO = new RoutePointDTO(
                routePointUuid, "Terminal La Plata", -34.9214, -57.9545, null, null, true
        );

        when(routePointRepository
                     .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                             companyUuid, routeUuid, routePointUuid)).thenReturn(Optional.of(routePoint));
        when(routePointRepository.save(any(RoutePoint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(routePointMapper.toDTO(any(RoutePoint.class))).thenReturn(expectedDTO);

        RoutePointDTO result = routePointService.updateRoutePoint(
                companyUuid, routeUuid, routePointUuid, request);

        assertEquals(expectedDTO, result);

        ArgumentCaptor<RoutePoint> captor = ArgumentCaptor.forClass(RoutePoint.class);
        verify(routePointRepository).save(captor.capture());

        RoutePoint saved = captor.getValue();
        assertSame(routePoint, saved);
        assertEquals("Terminal La Plata", saved.getName());
        assertEquals(-34.9214, saved.getLatitude());
        assertEquals(-57.9545, saved.getLongitude());

        verify(routePointMapper).toDTO(saved);
    }
}