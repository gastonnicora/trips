package com.gastonnicora.trips.unit.trip;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.trip.TripUpdate;
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
import com.gastonnicora.trips.exceptions.NotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTripTest {

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
    private UUID tripUuid;
    private UUID newVehicleUuid;
    private UUID newDriverUuid;
    private LocalDateTime date;

    private Route route;
    private Trip trip;
    private Vehicle currentVehicle;
    private Vehicle newVehicle;
    private User currentDriver;
    private User newDriver;
    private Worker newWorker;

    @BeforeEach
    void setUp() {
        companyUuid = UUID.randomUUID();
        tripUuid = UUID.randomUUID();
        newVehicleUuid = UUID.randomUUID();
        newDriverUuid = UUID.randomUUID();
        date = LocalDateTime.of(2030, 1, 1, 8, 0);

        Company company = new Company();
        company.setUuid(companyUuid);

        route = new Route(company, "Test route", PricingType.PER_KM);

        currentVehicle = new Vehicle();
        currentVehicle.setUuid(UUID.randomUUID());

        newVehicle = new Vehicle();
        newVehicle.setUuid(newVehicleUuid);

        currentDriver = new User();
        currentDriver.setUuid(UUID.randomUUID());

        newDriver = new User();
        newDriver.setUuid(newDriverUuid);

        newWorker = new Worker();
        newWorker.setUser(newDriver);
        newWorker.setRoles(Set.of(RoleCompany.DRIVER));

        trip = new Trip(route, date, currentVehicle, currentDriver, TypeTrip.TOUR, null);
    }

    @Test
    void shouldThrowNotFoundWhenTripDoesNotExist() {
        when(tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(tripUuid, companyUuid))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                                            () -> tripsService.updateTrip(companyUuid, tripUuid, tripUpdate(newVehicleUuid, newDriverUuid)));

        assertEquals("El viaje solicitado no existe", ex.getMessage());
        verifyNoInteractions(vehicleRepository, workerRepository);
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenVehicleDoesNotExist() {
        stubTrip();
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(newVehicleUuid, companyUuid))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.updateTrip(companyUuid, tripUuid, tripUpdate(newVehicleUuid, newDriverUuid)));

        assertEquals("El vehiculo solicitado no existe", ex.getMessage());
        assertSame(currentVehicle, trip.getVehicle());
        verifyNoInteractions(workerRepository);
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenDriverDoesNotExist() {
        stubTrip();
        stubNewVehicle();
        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                newDriverUuid, companyUuid)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.updateTrip(companyUuid, tripUuid, tripUpdate(newVehicleUuid, newDriverUuid)));

        assertEquals("El empleado solicitado no existe", ex.getMessage());
        assertSame(currentVehicle, trip.getVehicle());
        assertSame(currentDriver, trip.getDriver());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldThrowBadRequestWhenWorkerIsNotDriver() {
        newWorker.setRoles(Set.of(RoleCompany.SELLER));
        stubTrip();
        stubNewVehicle();
        stubNewWorker();

        BadRequestException ex = assertThrows(BadRequestException.class,
                                              () -> tripsService.updateTrip(companyUuid, tripUuid, tripUpdate(newVehicleUuid, newDriverUuid)));

        assertEquals("El empleado solicitado no existe", ex.getMessage());
        assertSame(currentDriver, trip.getDriver());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldUpdateVehicleAndDriverSuccessfully() {
        stubTrip();
        stubNewVehicle();
        stubNewWorker();
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripDTO tripDTO = new TripDTO();
        when(tripMapper.toDTO(any(Trip.class))).thenReturn(tripDTO);

        TripDTO result = tripsService.updateTrip(companyUuid, tripUuid, tripUpdate(newVehicleUuid, newDriverUuid));

        assertSame(tripDTO, result);

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).save(captor.capture());
        Trip saved = captor.getValue();

        assertSame(trip, saved);
        assertSame(newVehicle, saved.getVehicle());
        assertSame(newDriver, saved.getDriver());
        assertSame(route, saved.getRoute());
        assertEquals(date, saved.getDate());
        assertEquals(TypeTrip.TOUR, saved.getType());
    }

    // ---------- helpers ----------

    private TripUpdate tripUpdate(UUID vehicleUuid, UUID driverUuid) {
        TripUpdate tripUpdate = new TripUpdate();
        tripUpdate.setVehicleUuid(vehicleUuid);
        tripUpdate.setDriverUuid(driverUuid);
        return tripUpdate;
    }

    private void stubTrip() {
        when(tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(tripUuid, companyUuid))
                .thenReturn(Optional.of(trip));
    }

    private void stubNewVehicle() {
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(newVehicleUuid, companyUuid))
                .thenReturn(Optional.of(newVehicle));
    }

    private void stubNewWorker() {
        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                newDriverUuid, companyUuid)).thenReturn(Optional.of(newWorker));
    }
}