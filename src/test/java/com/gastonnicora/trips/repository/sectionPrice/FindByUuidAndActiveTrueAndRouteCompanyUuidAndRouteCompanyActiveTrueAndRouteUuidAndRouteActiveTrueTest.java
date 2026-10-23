package com.gastonnicora.trips.repository.sectionPrice;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.RoutePointRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.SectionPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueTest {

    @Autowired
    private SectionPriceRepository sectionPriceRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;
    private Route route;
    private SectionPrice sectionPrice;

    @BeforeEach
    void setup() {
        company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        route = routeRepository.save(new Route(company, "Test route", PricingType.PER_SECTION));

        RoutePoint from = createPoint(route, "A");
        RoutePoint to = createPoint(route, "B");

        sectionPrice = sectionPriceRepository.save(
                new SectionPrice(route, from, to, BigDecimal.valueOf(1000)));
    }

    private RoutePoint createPoint(Route owner, String name) {
        RoutePoint point = new RoutePoint(name, -34.92, -57.95);
        owner.addPoint(point);
        return routePointRepository.save(point);
    }

    private Optional<SectionPrice> find(UUID uuid, UUID companyUuid, UUID routeUuid) {
        return sectionPriceRepository
                .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                        uuid, companyUuid, routeUuid);
    }

    @Test
    void shouldReturnSectionPriceWhenAllConditionsMatch() {
        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), company.getUuid(), route.getUuid());

        assertTrue(found.isPresent());
        assertEquals(sectionPrice.getUuid(), found.get().getUuid());
    }

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<SectionPrice> found = find(
                UUID.randomUUID(), company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenCompanyDoesNotOwnTheRoute() {
        Company otherCompany = companyRepository.save(new Company(
                "Other", "La Plata, Argentina", -34.9214, -57.9545,
                "other@mail.com", "456"
        ));

        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), otherCompany.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenSectionPriceBelongsToAnotherRoute() {
        Route otherRoute = routeRepository.save(new Route(company, "Other route", PricingType.PER_SECTION));

        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), company.getUuid(), otherRoute.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenSectionPriceIsInactive() {
        sectionPrice.setActive(false);
        sectionPriceRepository.save(sectionPrice);

        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenRouteIsInactive() {
        route.setActive(false);
        routeRepository.save(route);

        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        company.setActive(false);
        companyRepository.save(company);

        Optional<SectionPrice> found = find(
                sectionPrice.getUuid(), company.getUuid(), route.getUuid());

        assertFalse(found.isPresent());
    }
}