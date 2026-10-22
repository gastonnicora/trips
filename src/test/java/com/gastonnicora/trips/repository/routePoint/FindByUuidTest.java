package com.gastonnicora.trips.repository.routePoint;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.RoutePointRepository;
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
public class FindByUuidTest {
    @Autowired
    private RoutePointRepository routePointRepository;


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<RoutePoint> found = routePointRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnRoutePointWhenUuidExists() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Route route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        RoutePoint routePoint = new RoutePoint("Test routePoint", 35.00, 36.00);
        routePoint.setRoute(route);
        routePoint = routePointRepository.save(routePoint);


        Optional<RoutePoint> found = routePointRepository.findByUuid(routePoint.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), routePoint.getUuid());
        assertEquals("Test routePoint", found.get().getName());
        assertEquals(35.00, found.get().getLatitude());
        assertEquals(36.00, found.get().getLongitude());
    }
}
