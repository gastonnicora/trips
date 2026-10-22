package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRoutesTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Test
    void shouldGetRoutesSuccessfully() {
        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        List<Route> routes = List.of(
                new Route(company, "Ruta 1", PricingType.PER_KM),
                new Route(company, "Ruta 2", PricingType.PER_SECTION)
                                    );
        List<RouteDTO> routeDTOs = List.of(new RouteDTO(), new RouteDTO());

        when(routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid())).thenReturn(
                routes);
        when(routeMapper.toDTOList(routes)).thenReturn(routeDTOs);

        List<RouteDTO> result = routeService.getRoutes(company.getUuid());

        assertEquals(routeDTOs, result);

        verify(routeRepository).findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());
        verify(routeMapper).toDTOList(routes);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyHasNoRoutes() {
        UUID companyUuid = UUID.randomUUID();

        when(routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(companyUuid)).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> routeService.getRoutes(companyUuid));

        verify(routeRepository).findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(companyUuid);
        verify(routeMapper, never()).toDTOList(anyList());
    }
}