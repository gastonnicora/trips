package com.gastonnicora.trips.unit.vehicle;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid())).thenReturn(Optional.of(vehicle));

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

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.deleteVehicle(company.getUuid(), vehicle.getUuid()));

        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(
                vehicle.getUuid(), company.getUuid());
        verify(vehicleRepository, never()).save(new Vehicle());
    }
}
