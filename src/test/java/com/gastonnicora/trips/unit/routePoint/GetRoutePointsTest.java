package com.gastonnicora.trips.unit.routePoint;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RoutePointMapper;
import com.gastonnicora.trips.repositories.RoutePointRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RoutePointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoutePointsTest {

    @InjectMocks
    private RoutePointService routePointService;

    @Mock
    private RoutePointRepository routePointRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RoutePointMapper routePointMapper;

    @Test
    void shouldReturnRoutePointDTOListWhenPointsExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        RoutePoint first = new RoutePoint("Punto A", -34.9214, -57.9545);
        RoutePoint second = new RoutePoint("Punto B", -34.9300, -57.9600);
        List<RoutePoint> points = List.of(first, second);

        List<RoutePointDTO> expectedDTOs = List.of(
                new RoutePointDTO(UUID.randomUUID(), "Punto A", -34.9214, -57.9545, null, null, true),
                new RoutePointDTO(UUID.randomUUID(), "Punto B", -34.9300, -57.9600, null, null, true)
                                                  );

        when(routePointRepository
                     .findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             companyUuid, routeUuid)).thenReturn(points);
        when(routePointMapper.toDTOList(points)).thenReturn(expectedDTOs);

        List<RoutePointDTO> result = routePointService.getRoutePoints(companyUuid, routeUuid);

        assertEquals(expectedDTOs, result);
        verify(routePointMapper).toDTOList(points);
    }

    @Test
    void shouldThrowNotFoundWhenRouteHasNoActivePoints() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routePointRepository
                     .findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                             companyUuid, routeUuid)).thenReturn(List.of());

        NotFoundException ex = assertThrows(NotFoundException.class,
                                            () -> routePointService.getRoutePoints(companyUuid, routeUuid));

        assertEquals("No existen puntos de rutas para la ruta.", ex.getMessage());
        verify(routePointMapper, never()).toDTOList(any());
    }
}