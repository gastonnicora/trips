package com.gastonnicora.trips.repository.bus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.repositories.BusRepository;
import com.gastonnicora.trips.repositories.CompanyRepository;

public class FindByCompanyUuidAndPlateTest {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByCompanyUuidAndPlate() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        company = companyRepository.save(company);

        Bus bus = new Bus(company, "ABC123", "Model X", 50);

        Bus b = busRepository.save(bus);

        Optional<Bus> found = busRepository.findByCompanyUuidAndPlate(company.getUuid(),"ABC123");

        assertFalse(found.isEmpty());
        assertTrue(found.get().getUuid().equals(b.getUuid()));
        assertTrue(found.get().getCompany().getUuid().equals(company.getUuid()));
        assertTrue(found.get().getPlate().equals("ABC123"));
    }

    @Test
    void shouldFindByPlateIfNotExist() {

        Optional<Bus> found = busRepository.findByCompanyUuidAndPlate(UUID.randomUUID(), "XYZ789");

        assertTrue(found.isEmpty());
    }

}
