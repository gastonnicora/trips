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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUuidAndCompanyUuidAndActiveTrueTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldNotReturnRouteFromOtherCompanies() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));
        Company otherCompany = companyRepository.save(new Company(
                "Other", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "other@mail.com", "456"
        ));

        Route route = routeRepository.save(new Route(company, "Ruta propia", PricingType.PER_KM));

        Optional<Route> found = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(
                route.getUuid(), otherCompany.getUuid());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnActiveRouteSuccessfully() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        Route route = routeRepository.save(new Route(company, "Ruta 1", PricingType.PER_KM));

        Optional<Route> found = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(
                route.getUuid(), company.getUuid());

        assertFalse(found.isEmpty());
        assertTrue(found.get().isActive());
        assertEquals(route.getUuid(), found.get().getUuid());
        assertEquals("Ruta 1", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenCompanyAndRouteDoNotExist() {
        Optional<Route> found = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(
                UUID.randomUUID(), UUID.randomUUID());

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

        Optional<Route> found = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(
                route.getUuid(), company.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenRouteUuidDoesNotBelongToCompanyRoutes() {
        Company company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        routeRepository.save(new Route(company, "Ruta 1", PricingType.PER_KM));

        Optional<Route> found = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(
                UUID.randomUUID(), company.getUuid());

        assertTrue(found.isEmpty());
    }

}
