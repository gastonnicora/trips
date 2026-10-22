package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRouteTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Test
    void shouldUpdateRouteSuccessfully() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        Route route = new Route(company, "Ruta vieja", PricingType.PER_KM);
        RouteDTO routeDTO = new RouteDTO();

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.of(route));
        when(routeRepository.save(route)).thenReturn(route);
        when(routeMapper.toDTO(route)).thenReturn(routeDTO);

        RouteDTO result = routeService.updateRoute(
                companyUuid,
                routeUuid,
                new RouteCreate("Ruta nueva", PricingType.PER_SECTION)
                                                  );

        assertSame(routeDTO, result);
        assertEquals("Ruta nueva", route.getName());
        assertEquals(PricingType.PER_SECTION, route.getPricingType());
        assertEquals(company, route.getCompany());

        verify(routeRepository).findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid);
        verify(routeRepository).save(route);
        verify(routeMapper).toDTO(route);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRouteDoesNotExistInCompany() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> routeService.updateRoute(
                        companyUuid,
                        routeUuid,
                        new RouteCreate("Ruta nueva", PricingType.PER_KM)
                                              )
                    );

        verify(routeRepository).findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid);
        verify(routeRepository, never()).save(any(Route.class));
        verify(routeMapper, never()).toDTO(any(Route.class));
    }
}