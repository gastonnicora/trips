package com.gastonnicora.trips.repository.vehicle;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FindByUuidAndActiveTrueTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldFindByUuidAndActiveTrue() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50));

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrue(vehicle.getUuid());

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(vehicle.getUuid()));
        assertTrue(found.get().isActive());
        assertTrue(found.get().getPlate().equals("ABC123"));
    }

    @Test
    void shouldNotFindByUuidAndActiveTrueIfNotExist() {
        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrue(UUID.randomUUID());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldNotFindByUuidAndActiveTrueIfInactive() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);
        vehicle.setActive(false);
        vehicle = vehicleRepository.save(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrue(vehicle.getUuid());

        assertFalse(found.isPresent());
    }
}
