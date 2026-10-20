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
public class FindByUuidAndActiveTrueTest {


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveRouteWhenUuidExists() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Route route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        Optional<Route> found = routeRepository.findByUuidAndActiveTrue(route.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), route.getUuid());
        assertTrue(found.get().isActive());
        assertEquals("Test route", found.get().getName());
        assertEquals(PricingType.PER_KM, found.get().getPricingType());
    }

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<Route> found = routeRepository.findByUuidAndActiveTrue(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Route route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));
        route.setActive(false);

        Optional<Route> found = routeRepository.findByUuidAndActiveTrue(route.getUuid());

        assertFalse(found.isPresent());
    }
}
