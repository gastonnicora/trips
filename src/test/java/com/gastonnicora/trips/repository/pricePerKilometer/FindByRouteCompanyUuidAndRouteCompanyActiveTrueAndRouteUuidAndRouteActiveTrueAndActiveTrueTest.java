package com.gastonnicora.trips.repository.pricePerKilometer;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.PricePerKilometerRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.junit.jupiter.api.BeforeEach;
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
class FindByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrueTest {

    @Autowired
    private PricePerKilometerRepository pricePerKilometerRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;
    private Route route;
    private PricePerKilometer pricePerKilometer;

    @BeforeEach
    void setup() {
        company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        pricePerKilometer = new PricePerKilometer(
                route, BigDecimal.valueOf(100), BigDecimal.valueOf(10000), BigDecimal.valueOf(10000));
        pricePerKilometer = pricePerKilometerRepository.save(pricePerKilometer);
    }

    private Optional<PricePerKilometer> find(UUID companyUuid, UUID routeUuid) {
        return pricePerKilometerRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        companyUuid, routeUuid);
    }

    @Test
    void shouldReturnPricePerKilometerWhenAllConditionsMatch() {
        Optional<PricePerKilometer> found = find(company.getUuid(), route.getUuid());

        assertTrue(found.isPresent());
        assertEquals(pricePerKilometer.getUuid(), found.get().getUuid());
    }

    @Test
    void shouldReturnEmptyWhenRouteBelongsToAnotherCompany() {
        Company otherCompany = companyRepository.save(new Company(
                "Other", "La Plata, Argentina", -34.9214, -57.9545,
                "other@mail.com", "456"
        ));

        Optional<PricePerKilometer> found = find(otherCompany.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenPricePerKilometerBelongsToAnotherRoute() {
        Route otherRoute = routeRepository.save(new Route(company, "Other route", PricingType.PER_KM));

        Optional<PricePerKilometer> found = find(company.getUuid(), otherRoute.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenPricePerKilometerIsInactive() {
        pricePerKilometer.setActive(false);
        pricePerKilometerRepository.save(pricePerKilometer);

        Optional<PricePerKilometer> found = find(company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        route.setActive(false);
        routeRepository.save(route);

        Optional<PricePerKilometer> found = find(company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        company.setActive(false);
        companyRepository.save(company);

        Optional<PricePerKilometer> found = find(company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRouteDoesNotExist() {
        Optional<PricePerKilometer> found = find(company.getUuid(), UUID.randomUUID());

        assertFalse(found.isPresent());
    }
}