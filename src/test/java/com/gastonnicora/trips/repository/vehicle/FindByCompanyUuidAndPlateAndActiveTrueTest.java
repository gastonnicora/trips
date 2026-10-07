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
class FindByCompanyUuidAndPlateAndActiveTrueTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveVehicleWhenCompanyAndPlateMatch() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50));

        Optional<Vehicle> found = vehicleRepository.findByCompanyUuidAndPlateAndActiveTrue(
                company.getUuid(), "ABC123");

        assertTrue(found.isPresent());
        assertTrue(found.get().getUuid().equals(vehicle.getUuid()));
        assertTrue(found.get().getCompany().getUuid().equals(company.getUuid()));
        assertTrue(found.get().getPlate().equals("ABC123"));
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldReturnEmptyWhenCompanyAndPlateDoNotMatch() {
        Optional<Vehicle> found = vehicleRepository.findByCompanyUuidAndPlateAndActiveTrue(
                UUID.randomUUID(), "ABC123");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenVehicleIsInactive() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByCompanyUuidAndPlateAndActiveTrue(
                company.getUuid(), "ABC123");

        assertFalse(found.isPresent());
    }
}
