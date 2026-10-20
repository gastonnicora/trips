package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRouteTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Test
    void shouldGetRouteSuccessfully() {
        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        RouteDTO routeDTO = new RouteDTO();

        when(routeRepository.findByUuidAndActiveTrue(route.getUuid())).thenReturn(Optional.of(route));
        when(routeMapper.toDTO(route)).thenReturn(routeDTO);

        RouteDTO result = routeService.getRoute(company.getUuid(), route.getUuid());

        assertEquals(routeDTO, result);

        verify(routeRepository).findByUuidAndActiveTrue(route.getUuid());
        verify(routeMapper).toDTO(route);
    }

    @Test
    void shouldThrowForbiddenExceptionWhenRouteBelongsToAnotherCompany() {
        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(UUID.randomUUID());

        when(routeRepository.findByUuidAndActiveTrue(route.getUuid())).thenReturn(Optional.of(route));

        assertThrows(ForbiddenException.class, () -> routeService.getRoute(UUID.randomUUID(), route.getUuid()));

        verify(routeRepository).findByUuidAndActiveTrue(route.getUuid());
        verify(routeMapper, never()).toDTO(any(Route.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRouteDoesNotExist() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndActiveTrue(routeUuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> routeService.getRoute(companyUuid, routeUuid));

        verify(routeRepository).findByUuidAndActiveTrue(routeUuid);
        verify(routeMapper, never()).toDTO(any(Route.class));
    }
}