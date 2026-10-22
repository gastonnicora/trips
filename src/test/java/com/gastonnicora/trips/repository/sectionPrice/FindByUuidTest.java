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
    private SectionPriceRepository sectionPriceRepository;

    @Autowired
    private RoutePointRepository routePointRepository;


    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Optional<SectionPrice> found = sectionPriceRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnSectionPriceWhenUuidExists() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Route route = routeRepository.save(new Route(company, "Test route", PricingType.PER_KM));

        RoutePoint routePoint = new RoutePoint("Test routePoint", 35.00, 36.00);
        routePoint.setRoute(route);

        routePoint = routePointRepository.save(routePoint);

        SectionPrice sectionPrice = new SectionPrice(route, routePoint, routePoint, BigDecimal.valueOf(35));

        sectionPrice = sectionPriceRepository.save(sectionPrice);

        Optional<SectionPrice> found = sectionPriceRepository.findByUuid(sectionPrice.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getPrice(), sectionPrice.getPrice());
        assertEquals(found.get().getRoute().getUuid(), route.getUuid());
        assertEquals(routePoint.getUuid(), found.get().getFromPoint().getUuid());
        assertEquals(routePoint.getUuid(), found.get().getToPoint().getUuid());
    }
}
