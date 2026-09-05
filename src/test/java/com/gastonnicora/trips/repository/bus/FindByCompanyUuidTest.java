package com.gastonnicora.trips.repository.bus;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.repositories.CompanyRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByCompanyUuidTest {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByCompanyUuid() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        company = companyRepository.save(company);

        Bus bus = new Bus(company, "ABC123", "Model X", 50);

        Bus b = busRepository.save(bus);

        List<Bus> found = busRepository.findAllByCompanyUuid(bus.getCompany().getUuid());

        assertFalse(found.isEmpty());
        assertTrue(found.get(0).getUuid().equals(b.getUuid()));
        assertTrue(found.get(0).getCompany().getUuid().equals(company.getUuid()));
        assertTrue(found.get(0).getPlate().equals("ABC123"));
    }

    @Test
    void shouldFindByCompanyUuidIfNotExist() {

        List<Bus> found = busRepository.findAllByCompanyUuid(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindByCompanyUuidIfExistMultiple() {

        Company company = new Company(
                "Test",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "test@mail.com",
                "123"
        );

        company = companyRepository.save(company);

        Bus bus = busRepository.save(
                new Bus(company, "ABC123", "Model X", 50)
        );

        Bus bus2 = busRepository.save(
                new Bus(company, "DEF456", "Model Y", 60)
        );

        Bus bus3 = busRepository.save(
                new Bus(company, "GHI789", "Model Z", 70)
        );

        List<Bus> found
                = busRepository.findAllByCompanyUuid(company.getUuid());

        assertEquals(3, found.size());

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(bus.getUuid())));

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(bus2.getUuid())));

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(bus3.getUuid())));
    }

}
