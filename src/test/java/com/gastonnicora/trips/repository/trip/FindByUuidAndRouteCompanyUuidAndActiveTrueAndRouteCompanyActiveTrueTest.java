package com.gastonnicora.trips.repository.trip;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrueTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void shouldReturnActiveTripWhenCompanyIsActive() {
        Company company = saveCompany("test@mail.com", "123", true);
        Trip trip = saveTrip(company, true);

        Optional<Trip> found = tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(
                trip.getUuid(), company.getUuid());

        assertTrue(found.isPresent());
        assertEquals(trip.getUuid(), found.get().getUuid());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldNotReturnTripFromOtherCompany() {
        Company company = saveCompany("test@mail.com", "123", true);
        Company otherCompany = saveCompany("other@mail.com", "456", true);
        Trip trip = saveTrip(company, true);

        Optional<Trip> found = tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(
                trip.getUuid(), otherCompany.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenTripIsInactive() {
        Company company = saveCompany("test@mail.com", "123", true);
        Trip trip = saveTrip(company, false);

        Optional<Trip> found = tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(
                trip.getUuid(), company.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenCompanyIsInactive() {
        Company company = saveCompany("test@mail.com", "123", false);
        Trip trip = saveTrip(company, true);

        Optional<Trip> found = tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(
                trip.getUuid(), company.getUuid());

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenTripDoesNotExist() {
        Optional<Trip> found = tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(
                UUID.randomUUID(), UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    // ---------- helpers ----------

    private Company saveCompany(String email, String phone, boolean active) {
        Company company = new Company(
                "Test", "Buenos Aires, Argentina", -34.6037, -58.3816, email, phone
        );
        company.setActive(active);
        return companyRepository.save(company);
    }

    private Trip saveTrip(Company company, boolean active) {
        Route route = routeRepository.save(new Route(company, "Ruta", PricingType.PER_KM));
        Vehicle vehicle = vehicleRepository.save(new Vehicle(company, "ABC123", "Model X", 50));
        User driver = userRepository.save(new User(
                "driver", "test", "driver@test.com", "password", Set.of(Role.USER)
        ));

        Trip trip = new Trip(route, LocalDateTime.of(2030, 1, 1, 8, 0), vehicle, driver, TypeTrip.TOUR, null);
        trip.setActive(active);
        return tripRepository.save(trip);
    }
}