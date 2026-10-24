package com.gastonnicora.trips.unit.route;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.services.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateRouteTest {

    @InjectMocks
    private RouteService routeService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    private Company company;

    @Test
    void shouldThrowNotFoundExceptionWhenRouteDoesNotExist() {
        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                                            () -> routeService.validateRoute(companyUuid, routeUuid));

        assertEquals("Ruta no encontrada", ex.getMessage());
    }

    @Test
    void shouldThrowBadRequestWhenRouteHasLessThanTwoPoints() {
        Route route = givenRoute(PricingType.PER_KM);
        addPoints(route, 1);
        givenFoundRoute(route);

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> routeService.validateRoute(company.getUuid(), route.getUuid()));

        assertEquals("La ruta debe tener al menos dos puntos", ex.getMessage());
    }

    @Test
    void shouldThrowBadRequestWhenPerKmRouteHasNoPricePerKilometer() {
        Route route = givenRoute(PricingType.PER_KM);
        addPoints(route, 2);
        givenFoundRoute(route);

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> routeService.validateRoute(company.getUuid(), route.getUuid()));

        assertEquals("La ruta por kilómetro no tiene precio por kilómetro configurado", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenPerKmRouteHasPricePerKilometer() {
        Route route = givenRoute(PricingType.PER_KM);
        addPoints(route, 2);
        route.setPricePerKilometer(new PricePerKilometer());
        givenFoundRoute(route);

        assertDoesNotThrow(() -> routeService.validateRoute(company.getUuid(), route.getUuid()));
    }

    @Test
    void shouldThrowBadRequestWhenPerSectionRouteHasNoSectionPrices() {
        Route route = givenRoute(PricingType.PER_SECTION);
        addPoints(route, 2);
        givenFoundRoute(route);

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> routeService.validateRoute(company.getUuid(), route.getUuid()));

        assertEquals("La ruta por tramo no tiene precios de tramo configurados", ex.getMessage());
    }

    @Test
    void shouldThrowBadRequestWhenLastSectionDoesNotEndAtLastPoint() {
        Route route = givenRoute(PricingType.PER_SECTION);
        List<RoutePoint> points = addPoints(route, 3);
        addSection(route, points.get(0), points.get(1));
        givenFoundRoute(route);

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> routeService.validateRoute(company.getUuid(), route.getUuid()));

        assertEquals("El último tramo no termina en el último punto de la ruta", ex.getMessage());
    }

    @Test
    void shouldNotThrowWhenLastSectionEndsAtLastPoint() {
        Route route = givenRoute(PricingType.PER_SECTION);
        List<RoutePoint> points = addPoints(route, 3);
        addSection(route, points.get(0), points.get(1));
        addSection(route, points.get(1), points.get(2));
        givenFoundRoute(route);

        assertDoesNotThrow(() -> routeService.validateRoute(company.getUuid(), route.getUuid()));
    }

    // ---------- helpers ----------

    private Route givenRoute(PricingType pricingType) {
        company = new Company();
        company.setUuid(UUID.randomUUID());

        Route route = new Route(company, "Test route", pricingType);
        route.setUuid(UUID.randomUUID());
        return route;
    }

    private void givenFoundRoute(Route route) {
        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(
                route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));
    }

    private List<RoutePoint> addPoints(Route route, int count) {
        List<RoutePoint> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RoutePoint point = new RoutePoint();
            point.setUuid(UUID.randomUUID());
            route.addPoint(point);
            points.add(point);
        }
        return points;
    }

    private void addSection(Route route, RoutePoint from, RoutePoint to) {
        SectionPrice section = new SectionPrice();
        section.setFromPoint(from);
        section.setToPoint(to);
        route.addSectionPrice(section);
    }
}