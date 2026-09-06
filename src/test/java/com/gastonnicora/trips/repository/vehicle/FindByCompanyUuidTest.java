package com.gastonnicora.trips.repository.vehicle;

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

import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.repositories.CompanyRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByCompanyUuidTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByCompanyUuid() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);

        Vehicle b = vehicleRepository.save(vehicle);

        List<Vehicle> found = vehicleRepository.findAllByCompanyUuid(vehicle.getCompany().getUuid());

        assertFalse(found.isEmpty());
        assertTrue(found.get(0).getUuid().equals(b.getUuid()));
        assertTrue(found.get(0).getCompany().getUuid().equals(company.getUuid()));
        assertTrue(found.get(0).getPlate().equals("ABC123"));
    }

    @Test
    void shouldFindByCompanyUuidIfNotExist() {

        List<Vehicle> found = vehicleRepository.findAllByCompanyUuid(UUID.randomUUID());

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

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50)
        );

        Vehicle vehicle2 = vehicleRepository.save(
                new Vehicle(company, "DEF456", "Model Y", 60)
        );

        Vehicle vehicle3 = vehicleRepository.save(
                new Vehicle(company, "GHI789", "Model Z", 70)
        );

        List<Vehicle> found
                = vehicleRepository.findAllByCompanyUuid(company.getUuid());

        assertEquals(3, found.size());

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(vehicle.getUuid())));

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(vehicle2.getUuid())));

        assertTrue(found.stream()
                .anyMatch(b -> b.getUuid().equals(vehicle3.getUuid())));
    }

}
