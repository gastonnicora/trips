package com.gastonnicora.trips.unit.vehicle;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;

@ExtendWith(MockitoExtension.class)
class DeleteVehicleTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;


    @Test
    void shouldDeleteVehicleSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());

        Vehicle vehicle = new Vehicle();
        vehicle.setActive(true);
        vehicle.setCompany(company);

        when(vehicleRepository.findByUuidAndActiveTrue(vehicle.getUuid())).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(company.getUuid(), vehicle.getUuid());

        verify(vehicleRepository).save(vehicle);
        assertFalse(vehicle.isActive());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenVehicleDoesNotExist() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());

        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);

        when(vehicleRepository.findByUuidAndActiveTrue(vehicle.getUuid())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.deleteVehicle(company.getUuid(), vehicle.getUuid()));

        verify(vehicleRepository).findByUuidAndActiveTrue(vehicle.getUuid());
        verify(vehicleRepository, never()).save(new Vehicle());
    }
}
