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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueTest {

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
    private SectionPrice firstSection;
    private SectionPrice secondSection;

    @BeforeEach
    void setup() {
        company = companyRepository.save(new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        ));

        route = routeRepository.save(new Route(company, "Test route", PricingType.PER_SECTION));

        RoutePoint a = createPoint(route, "A");
        RoutePoint b = createPoint(route, "B");
        RoutePoint c = createPoint(route, "C");

        firstSection = sectionPriceRepository.save(
                new SectionPrice(route, a, b, BigDecimal.valueOf(1000)));
        secondSection = sectionPriceRepository.save(
                new SectionPrice(route, b, c, BigDecimal.valueOf(2000)));
    }

    private RoutePoint createPoint(Route owner, String name) {
        RoutePoint point = new RoutePoint(name, -34.92, -57.95);
        owner.addPoint(point);
        return routePointRepository.save(point);
    }

    private List<SectionPrice> find(UUID companyUuid, UUID routeUuid) {
        return sectionPriceRepository
                .findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                        companyUuid, routeUuid);
    }

    private Set<UUID> uuidsOf(List<SectionPrice> sections) {
        return sections.stream().map(SectionPrice::getUuid).collect(Collectors.toSet());
    }

    @Test
    void shouldReturnAllActiveSectionsOfRoute() {
        List<SectionPrice> found = find(company.getUuid(), route.getUuid());

        assertEquals(2, found.size());
        assertEquals(Set.of(firstSection.getUuid(), secondSection.getUuid()), uuidsOf(found));
    }

    @Test
    void shouldExcludeInactiveSections() {
        secondSection.setActive(false);
        sectionPriceRepository.save(secondSection);

        List<SectionPrice> found = find(company.getUuid(), route.getUuid());

        assertEquals(Set.of(firstSection.getUuid()), uuidsOf(found));
    }

    @Test
    void shouldExcludeSectionsOfAnotherRouteOfSameCompany() {
        Route otherRoute = routeRepository.save(new Route(company, "Other route", PricingType.PER_SECTION));
        RoutePoint x = createPoint(otherRoute, "X");
        RoutePoint y = createPoint(otherRoute, "Y");
        SectionPrice otherSection = sectionPriceRepository.save(
                new SectionPrice(otherRoute, x, y, BigDecimal.valueOf(500)));

        List<SectionPrice> found = find(company.getUuid(), route.getUuid());

        assertEquals(2, found.size());
        assertFalse(uuidsOf(found).contains(otherSection.getUuid()));
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
    void shouldReturnEmptyWhenRouteHasNoSections() {
        Route emptyRoute = routeRepository.save(new Route(company, "Empty route", PricingType.PER_SECTION));

        assertTrue(find(company.getUuid(), emptyRoute.getUuid()).isEmpty());
    }
}