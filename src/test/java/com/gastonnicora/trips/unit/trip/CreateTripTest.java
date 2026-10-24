package com.gastonnicora.trips.unit.trip;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.TripsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTripTest {

    @InjectMocks
    private TripsService tripsService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMapper tripMapper;

    private UUID companyUuid;
    private UUID routeUuid;
    private UUID vehicleUuid;
    private UUID driverUuid;
    private LocalDateTime date;

    private Route route;
    private Vehicle vehicle;
    private User driver;
    private Worker worker;

    @BeforeEach
    void setUp() {
        companyUuid = UUID.randomUUID();
        routeUuid = UUID.randomUUID();
        vehicleUuid = UUID.randomUUID();
        driverUuid = UUID.randomUUID();
        date = LocalDateTime.of(2030, 1, 1, 8, 0);

        Company company = new Company();
        company.setUuid(companyUuid);

        route = new Route(company, "Test route", PricingType.PER_KM);
        route.setUuid(routeUuid);

        vehicle = new Vehicle();
        vehicle.setUuid(vehicleUuid);

        driver = new User();
        driver.setUuid(driverUuid);

        worker = new Worker();
        worker.setUser(driver);
        worker.setRoles(Set.of(RoleCompany.DRIVER));
    }

    @Test
    void shouldThrowBadRequestWhenRouteDoesNotExist() {
        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.createTrip(companyUuid, tripCreate(TypeTrip.TOUR, null)));

        assertEquals("La ruta solicitada no existe", ex.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenVehicleDoesNotExist() {
        stubRoute();
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid, companyUuid))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.createTrip(companyUuid, tripCreate(TypeTrip.TOUR, null)));

        assertEquals("El vehiculo solicitado no existe", ex.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenWorkerDoesNotExist() {
        stubRoute();
        stubVehicle();
        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                driverUuid, companyUuid)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.createTrip(companyUuid, tripCreate(TypeTrip.TOUR, null)));

        assertEquals("El empleado solicitado no existe", ex.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenWorkerIsNotDriver() {
        worker.setRoles(Set.of(RoleCompany.OWNER));
        stubRoute();
        stubVehicle();
        stubWorker();

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.createTrip(companyUuid, tripCreate(TypeTrip.TOUR, null)));

        assertEquals("El empleado solicitado no existe", ex.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldCreateTripSuccessfully() {
        stubRoute();
        stubVehicle();
        stubWorker();
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripDTO tripDTO = new TripDTO();
        when(tripMapper.toDTO(any(Trip.class))).thenReturn(tripDTO);

        TripDTO result = tripsService.createTrip(companyUuid, tripCreate(TypeTrip.DOMICILE, 5));

        assertSame(tripDTO, result);

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());
        Trip saved = captor.getValue();

        assertSame(route, saved.getRoute());
        assertEquals(date, saved.getDate());
        assertSame(vehicle, saved.getVehicle());
        assertSame(driver, saved.getDriver());
        assertEquals(TypeTrip.DOMICILE, saved.getType());
        assertEquals(5, saved.getDistanceToRoute());
        assertTrue(saved.isActive());
    }

    // ---------- helpers ----------

    private TripCreate tripCreate(TypeTrip type, Integer distanceToRoute) {
        return new TripCreate(routeUuid, date, vehicleUuid, driverUuid, type, distanceToRoute);
    }

    private void stubRoute() {
        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.of(route));
    }

    private void stubVehicle() {
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid, companyUuid))
                .thenReturn(Optional.of(vehicle));
    }

    private void stubWorker() {
        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                driverUuid, companyUuid)).thenReturn(Optional.of(worker));
    }
}