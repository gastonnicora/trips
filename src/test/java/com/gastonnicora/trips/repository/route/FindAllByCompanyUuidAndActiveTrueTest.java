package com.gastonnicora.trips.repository.route;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindAllByCompanyUuidAndActiveTrueTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldNotReturnRoutesFromOtherCompanies() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));
        Company otherCompany = companyRepository.save(new Company(
                "Other", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "other@mail.com", "456"
        ));

        Route route = routeRepository.save(new Route(company, "Ruta propia", PricingType.PER_KM));
        routeRepository.save(new Route(otherCompany, "Ruta ajena", PricingType.PER_KM));

        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals(route.getUuid(), found.getFirst().getUuid());
    }

    @Test
    void shouldReturnActiveRoutesWhenCompanyUuidExists() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        Route route = routeRepository.save(new Route(company, "Ruta 1", PricingType.PER_KM));
        Route route2 = routeRepository.save(new Route(company, "Ruta 2", PricingType.PER_SECTION));

        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(item -> item.getUuid().equals(route.getUuid())));
        assertTrue(found.stream().anyMatch(item -> item.getUuid().equals(route2.getUuid())));
        assertTrue(found.stream().allMatch(Route::isActive));
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company.setActive(false);
        companyRepository.save(company);
        Route route = new Route(company, "Ruta inactiva", PricingType.PER_KM);
        route.setActive(false);
        routeRepository.save(route);

        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenCompanyUuidDoesNotExist() {
        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        Route route = new Route(company, "Ruta inactiva", PricingType.PER_KM);
        route.setActive(false);
        routeRepository.save(route);

        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnOnlyActiveRoutesWhenCompanyHasActiveAndInactiveRoutes() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        Route active = routeRepository.save(new Route(company, "Ruta activa", PricingType.PER_KM));
        Route inactive = new Route(company, "Ruta inactiva", PricingType.PER_KM);
        inactive.setActive(false);
        routeRepository.save(inactive);

        List<Route> found = routeRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertEquals(1, found.size());
        assertEquals(active.getUuid(), found.getFirst().getUuid());
    }
}