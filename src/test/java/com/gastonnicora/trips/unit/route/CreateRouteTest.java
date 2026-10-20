package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateRouteTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Test
    void shouldCreateRouteSuccessfully() {
        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", PricingType.PER_KM);

        RouteDTO routeDTO = new RouteDTO();

        when(routeRepository.save(any(Route.class)))
                .thenReturn(route);

        when(routeMapper.toDTO(route)).thenReturn(routeDTO);

        RouteDTO result = routeService.createRoute(
                company,
                new RouteCreate("Test route", PricingType.PER_KM)
                                                  );

        assertNotNull(result);
        assertEquals(routeDTO, result);

        ArgumentCaptor<Route> captor
                = ArgumentCaptor.forClass(Route.class);

        verify(routeRepository).save(captor.capture());

        Route routeSaved = captor.getValue();

        assertEquals(company, routeSaved.getCompany());
        assertEquals(PricingType.PER_KM, routeSaved.getPricingType());
        assertEquals("Test route", routeSaved.getName());

        verify(routeMapper).toDTO(route);
    }
}
