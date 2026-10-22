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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrueTest {

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;
    private Route route;
    private RoutePoint routePoint;

    @BeforeEach
    void setup() {
        company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        routePoint = new RoutePoint("Test routePoint", 35.00, 36.00);
        routePoint.setRoute(route);
        routePoint = routePointRepository.save(routePoint);
    }

    private Optional<RoutePoint> find(UUID companyUuid, UUID routeUuid, UUID routePointUuid) {
        return routePointRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                        companyUuid, routeUuid, routePointUuid);
    }

    @Test
    void shouldReturnRoutePointWhenAllConditionsMatch() {
        Optional<RoutePoint> found = find(company.getUuid(), route.getUuid(), routePoint.getUuid());

        assertTrue(found.isPresent());
        assertEquals(routePoint.getUuid(), found.get().getUuid());
        assertEquals("Test routePoint", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenRouteBelongsToAnotherCompany() {
        Company otherCompany = companyRepository.save(new Company(
                "Other", "La Plata, Argentina", -34.9214, -57.9545,
                "other@mail.com", "456"
        ));

        Optional<RoutePoint> found = find(otherCompany.getUuid(), route.getUuid(), routePoint.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenPointBelongsToAnotherRouteOfSameCompany() {
        Route otherRoute = routeRepository.save(new Route(company, "Other route", PricingType.PER_KM));

        Optional<RoutePoint> found = find(company.getUuid(), otherRoute.getUuid(), routePoint.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRoutePointIsInactive() {
        routePoint.setActive(false);
        routePointRepository.save(routePoint);

        Optional<RoutePoint> found = find(company.getUuid(), route.getUuid(), routePoint.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        route.setActive(false);
        routeRepository.save(route);

        Optional<RoutePoint> found = find(company.getUuid(), route.getUuid(), routePoint.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        company.setActive(false);
        companyRepository.save(company);

        Optional<RoutePoint> found = find(company.getUuid(), route.getUuid(), routePoint.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRoutePointDoesNotExist() {
        Optional<RoutePoint> found = find(company.getUuid(), route.getUuid(), UUID.randomUUID());

        assertFalse(found.isPresent());
    }
}