package com.gastonnicora.trips.repository.bus;

import java.util.Optional;
import java.util.UUID;

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
public class FindByUuidTest {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByUuid() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Bus bus = new Bus(company, "ABC123", "Model X", 50);

        Bus b = busRepository.save(bus);

        Optional<Bus> found = busRepository.findByUuid(bus.getUuid());

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(b.getUuid()));
        assertTrue(found.get().getCompany().getUuid().equals(c.getUuid()));
        assertTrue(found.get().getPlate().equals("ABC123"));
    }

    @Test
    void shouldFindByUuidIfNotExist() {

        Optional<Bus> found = busRepository.findByUuid(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindById() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        Company c = companyRepository.save(company);

        Bus bus = new Bus(company, "ABC123", "Model X", 50);
        Bus b = busRepository.save(bus);

        Optional<Bus> found = busRepository.findById(b.getUuid());

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(b.getUuid()));
        assertTrue(found.get().getPlate().equals("ABC123"));
    }
}
