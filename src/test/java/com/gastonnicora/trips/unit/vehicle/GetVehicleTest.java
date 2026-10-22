package com.gastonnicora.trips.unit.vehicle;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetVehicleTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void shouldReturnVehicleSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setActive(true);
        vehicle.setCompany(company);
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicle.getUuid(),company.getUuid())).thenReturn(Optional.of(vehicle));
        VehicleDTO expectedVehicleDTO = new VehicleDTO();
        when(vehicleMapper.toDTO(vehicle)).thenReturn(expectedVehicleDTO);
        VehicleDTO result = vehicleService.getVehicle(vehicle.getUuid(), company.getUuid());
        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicle.getUuid(),company.getUuid());
        assertEquals(expectedVehicleDTO, result);
        verify(vehicleMapper).toDTO(vehicle);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenVehicleDoesNotExist() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicle.getUuid(),company.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> vehicleService.getVehicle(vehicle.getUuid(), company.getUuid()));
        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(vehicle.getUuid(),company.getUuid());
        verify(vehicleMapper, never()).toDTO(any(Vehicle.class));
    }
}
