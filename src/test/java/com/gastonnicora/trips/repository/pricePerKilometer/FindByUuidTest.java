package com.gastonnicora.trips.repository.pricePerKilometer;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.PricePerKilometerRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUuidTest {

    @Autowired
    private PricePerKilometerRepository pricePerKilometerRepository;


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<PricePerKilometer> found = pricePerKilometerRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnPricePerKilometerWhenUuidExists() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Route route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        PricePerKilometer pricePerKilometer = new PricePerKilometer(
                route, BigDecimal.valueOf(30), BigDecimal.valueOf(30), BigDecimal.valueOf(30));

        pricePerKilometer = pricePerKilometerRepository.save(pricePerKilometer);

        Optional<PricePerKilometer> found = pricePerKilometerRepository.findByUuid(pricePerKilometer.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), pricePerKilometer.getUuid());
        assertEquals(found.get().getRoute().getUuid(), route.getUuid());
        assertEquals(found.get().getBaseKm(), pricePerKilometer.getBaseKm());
        assertEquals(found.get().getBasePrice(), pricePerKilometer.getBasePrice());
        assertEquals(found.get().getPricePerKm(), pricePerKilometer.getPricePerKm());
    }
}
