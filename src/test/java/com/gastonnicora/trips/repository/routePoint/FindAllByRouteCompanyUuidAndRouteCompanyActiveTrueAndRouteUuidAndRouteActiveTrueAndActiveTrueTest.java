package com.gastonnicora.trips.repository.routePoint;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.RoutePointRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrueTest {

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;
    private Route route;
    private RoutePoint pointA;
    private RoutePoint pointB;

    @BeforeEach
    void setup() {
        company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        pointA = savePoint(route, "Punto A");
        pointB = savePoint(route, "Punto B");
    }

    private RoutePoint savePoint(Route targetRoute, String name) {
        RoutePoint point = new RoutePoint(name, 35.00, 36.00);
        point.setRoute(targetRoute);
        return routePointRepository.save(point);
    }

    private List<RoutePoint> find(UUID companyUuid, UUID routeUuid) {
        return routePointRepository
                .findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        companyUuid, routeUuid);
    }

    private Set<UUID> uuidsOf(List<RoutePoint> points) {
        return points.stream().map(RoutePoint::getUuid).collect(Collectors.toSet());
    }

    @Test
    void shouldReturnAllActivePointsOfRoute() {
        List<RoutePoint> found = find(company.getUuid(), route.getUuid());

        assertEquals(2, found.size());
        assertEquals(Set.of(pointA.getUuid(), pointB.getUuid()), uuidsOf(found));
    }

    @Test
    void shouldExcludeInactivePoints() {
        pointB.setActive(false);
        routePointRepository.save(pointB);

        List<RoutePoint> found = find(company.getUuid(), route.getUuid());

        assertEquals(Set.of(pointA.getUuid()), uuidsOf(found));
    }

    @Test
    void shouldExcludePointsOfAnotherRouteOfSameCompany() {
        Route otherRoute = routeRepository.save(new Route(company, "Other route", PricingType.PER_KM));
        RoutePoint otherPoint = savePoint(otherRoute, "Punto otra ruta");

        List<RoutePoint> found = find(company.getUuid(), route.getUuid());

        assertEquals(2, found.size());
        assertFalse(uuidsOf(found).contains(otherPoint.getUuid()));
    }

    @Test
    void shouldReturnEmptyWhenRouteBelongsToAnotherCompany() {
        Company otherCompany = companyRepository.save(new Company(
                "Other", "La Plata, Argentina", -34.9214, -57.9545,
                "other@mail.com", "456"
        ));

        assertTrue(find(otherCompany.getUuid(), route.getUuid()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        route.setActive(false);
        routeRepository.save(route);

        assertTrue(find(company.getUuid(), route.getUuid()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        company.setActive(false);
        companyRepository.save(company);

        assertTrue(find(company.getUuid(), route.getUuid()).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenRouteHasNoPoints() {
        Route emptyRoute = routeRepository.save(new Route(company, "Empty route", PricingType.PER_KM));

        assertTrue(find(company.getUuid(), emptyRoute.getUuid()).isEmpty());
    }
}