package com.gastonnicora.trips.unit.vehicle;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVehicleTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    private Company buildCompany(UUID companyUuid) {
        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);
        return company;
    }

    @Test
    void shouldUpdateVehicleSuccessfullyWhenPlateChangesAndIsAvailable() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();
        Company company = buildCompany(companyUuid);

        Vehicle vehicle = new Vehicle(company, "AA123BB", "Mercedes Benz", 50);
        vehicle.setUuid(vehicleUuid);

        VehicleCreate vehicleUpdate = new VehicleCreate("CC456DD", "Iveco", 30);

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setUuid(vehicleUuid);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(
                companyUuid,
                vehicleUpdate.getPlate()
                                                                                         )).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleMapper.toDTO(vehicle)).thenReturn(expectedDTO);

        VehicleDTO result = vehicleService.updateVehicle(companyUuid, vehicleUuid, vehicleUpdate);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid);
        verify(vehicleRepository).findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(
                companyUuid,
                vehicleUpdate.getPlate()
                                                                                            );

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());

        Vehicle saved = captor.getValue();
        assertEquals(vehicleUuid, saved.getUuid());
        assertEquals(company, saved.getCompany());
        assertEquals("CC456DD", saved.getPlate());
        assertEquals("Iveco", saved.getModel());
        assertEquals(30, saved.getCapacity());
        assertTrue(saved.isActive());

        verify(vehicleMapper).toDTO(vehicle);
    }

    @Test
    void shouldUpdateVehicleWithoutCheckingPlateWhenPlateIsTheSame() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();
        Company company = buildCompany(companyUuid);

        Vehicle vehicle = new Vehicle(company, "AA123BB", "Mercedes Benz", 50);
        vehicle.setUuid(vehicleUuid);

        VehicleCreate vehicleUpdate = new VehicleCreate("AA123BB", "Iveco", 30);

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setUuid(vehicleUuid);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleMapper.toDTO(vehicle)).thenReturn(expectedDTO);

        VehicleDTO result = vehicleService.updateVehicle(companyUuid, vehicleUuid, vehicleUpdate);

        assertEquals(expectedDTO, result);

        verify(vehicleRepository, never())
                .findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(any(UUID.class), anyString());

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());

        Vehicle saved = captor.getValue();
        assertEquals("AA123BB", saved.getPlate());
        assertEquals("Iveco", saved.getModel());
        assertEquals(30, saved.getCapacity());

        verify(vehicleMapper).toDTO(vehicle);
    }

    @Test
    void shouldNotCheckPlateWhenNewPlateIsEqualAfterNormalization() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();
        Company company = buildCompany(companyUuid);

        Vehicle vehicle = new Vehicle(company, "AA123BB", "Mercedes Benz", 50);
        vehicle.setUuid(vehicleUuid);

        // VehicleCreate elimina espacios y pasa a mayúsculas: "aa 123 bb" -> "AA123BB"
        VehicleCreate vehicleUpdate = new VehicleCreate("aa 123 bb", "Mercedes Benz", 60);

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setUuid(vehicleUuid);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(vehicleMapper.toDTO(vehicle)).thenReturn(expectedDTO);

        VehicleDTO result = vehicleService.updateVehicle(companyUuid, vehicleUuid, vehicleUpdate);

        assertEquals(expectedDTO, result);

        verify(vehicleRepository, never())
                .findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(any(UUID.class), anyString());

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());

        assertEquals("AA123BB", captor.getValue().getPlate());
        assertEquals(60, captor.getValue().getCapacity());
    }

    @Test
    void shouldThrowConflictWhenNewPlateIsAlreadyRegisteredInCompany() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();
        Company company = buildCompany(companyUuid);

        Vehicle vehicle = new Vehicle(company, "AA123BB", "Mercedes Benz", 50);
        vehicle.setUuid(vehicleUuid);

        VehicleCreate vehicleUpdate = new VehicleCreate("CC456DD", "Iveco", 30);

        Vehicle otherVehicle = new Vehicle(company, "CC456DD", "Ford", 20);
        otherVehicle.setUuid(UUID.randomUUID());

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(
                companyUuid,
                vehicleUpdate.getPlate()
                                                                                         )).thenReturn(Optional.of(otherVehicle));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> vehicleService.updateVehicle(companyUuid, vehicleUuid, vehicleUpdate)
        );

        assertEquals(
                "No se puede guardar el cambio por que la patente ya esta registrada.",
                exception.getMessage()
        );

        verify(vehicleRepository).findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(
                companyUuid,
                vehicleUpdate.getPlate()
                                                                                            );
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(vehicleMapper, never()).toDTO(any(Vehicle.class));

        // el vehículo original no debe haberse modificado
        assertEquals("AA123BB", vehicle.getPlate());
        assertEquals("Mercedes Benz", vehicle.getModel());
        assertEquals(50, vehicle.getCapacity());
    }


    @Test
    void shouldThrowNotFoundWhenVehicleDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();
        VehicleCreate vehicleUpdate = new VehicleCreate("CC456DD", "Iveco", 30);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> vehicleService.updateVehicle(companyUuid, vehicleUuid, vehicleUpdate)
        );

        assertEquals("Vehículo no encontrado", exception.getMessage());

        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicleUuid,companyUuid);
        verify(vehicleRepository, never())
                .findByCompanyUuidAndCompanyActiveTrueAndPlateAndActiveTrue(any(UUID.class), anyString());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(vehicleMapper, never()).toDTO(any(Vehicle.class));
    }
}