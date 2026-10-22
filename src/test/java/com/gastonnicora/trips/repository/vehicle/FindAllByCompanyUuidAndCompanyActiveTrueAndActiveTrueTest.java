package com.gastonnicora.trips.repository.vehicle;


import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindAllByCompanyUuidAndCompanyActiveTrueAndActiveTrueTest {


    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveVehicleWhenUuidExists() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50));

        Vehicle vehicle2 = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50));

        List<Vehicle> found = vehicleRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertFalse(found.isEmpty());
        assertTrue(found.stream().anyMatch(item -> item.getUuid().equals(vehicle.getUuid())));
        assertTrue(found.stream().anyMatch(item -> item.getUuid().equals(vehicle2.getUuid())));
        assertTrue(found.stream().allMatch(Vehicle::isActive));
    }

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        List<Vehicle> found = vehicleRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenVehicleIsInactive() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);
        vehicle.setActive(false);
        vehicle = vehicleRepository.save(vehicle);

        List<Vehicle> found = vehicleRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertTrue(found.isEmpty());
    }
}
