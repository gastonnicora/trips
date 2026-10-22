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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrueTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveVehicleWhenUuidExists() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, "ABC123", "Model X", 50));

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid());

        assertTrue(found.isPresent());
        assertEquals(found.get().getUuid(), vehicle.getUuid());
        assertTrue(found.get().isActive());
        assertEquals("ABC123", found.get().getPlate());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company.setActive(false);
        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);
        vehicle = vehicleRepository.save(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenUuidDoesNotExist() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                UUID.randomUUID(), company.getUuid());

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenVehicleIsInactive() {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123"
        );
        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);
        vehicle.setActive(false);
        vehicle = vehicleRepository.save(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid());

        assertFalse(found.isPresent());
    }
}
