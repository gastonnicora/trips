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
class FindByCompanyUuidAndPlateTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnEmptyWhenCompanyAndPlateDoNotMatch() {

        Optional<Vehicle> found = vehicleRepository.findByCompanyUuidAndPlate(UUID.randomUUID(), "XYZ789");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnVehicleWhenCompanyAndPlateMatch() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");

        company = companyRepository.save(company);

        Vehicle vehicle = new Vehicle(company, "ABC123", "Model X", 50);

        Vehicle b = vehicleRepository.save(vehicle);

        Optional<Vehicle> found = vehicleRepository.findByCompanyUuidAndPlate(company.getUuid(), "ABC123");

        assertFalse(found.isEmpty());
        assertEquals(found.get().getUuid(), b.getUuid());
        assertEquals(found.get().getCompany().getUuid(), company.getUuid());
        assertEquals("ABC123", found.get().getPlate());
    }

}
