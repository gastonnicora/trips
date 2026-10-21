package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteRouteTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Test
    void shouldDeactivateRouteSuccessfully() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        Route route = new Route(company, "Ruta 1", PricingType.PER_KM);
        assertTrue(route.isActive());

        when(routeRepository.findByUuidAndCompanyUuidAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.of(route));

        routeService.deleteRoute(companyUuid, routeUuid);

        ArgumentCaptor<Route> captor = ArgumentCaptor.forClass(Route.class);

        verify(routeRepository).findByUuidAndCompanyUuidAndActiveTrue(routeUuid, companyUuid);
        verify(routeRepository).save(captor.capture());

        assertFalse(captor.getValue().isActive());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRouteDoesNotExistInCompany() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndCompanyUuidAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> routeService.deleteRoute(companyUuid, routeUuid));

        verify(routeRepository).findByUuidAndCompanyUuidAndActiveTrue(routeUuid, companyUuid);
        verify(routeRepository, never()).save(any(Route.class));
    }
}