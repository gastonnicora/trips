package com.gastonnicora.trips.unit.vehicle;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;

@ExtendWith(MockitoExtension.class)
public class GetVehicleTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void shouldGetVehicleSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setActive(true);
        vehicle.setCompany(company);
        when(vehicleRepository.findByUuid(vehicle.getUuid())).thenReturn(Optional.of(vehicle));
        VehicleDTO expectedVehicleDTO = new VehicleDTO();
        when(vehicleMapper.toDTO(vehicle)).thenReturn(expectedVehicleDTO);
        VehicleDTO result = vehicleService.getVehicle(vehicle.getUuid());
        verify(vehicleRepository).findByUuid(vehicle.getUuid());
        assertEquals(expectedVehicleDTO, result);
        verify(vehicleMapper).toDTO(vehicle);
    }

    @Test
    void shouldNotGetVehicleIfNotFound() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        when(vehicleRepository.findByUuid(vehicle.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> vehicleService.getVehicle(vehicle.getUuid()));
        verify(vehicleRepository).findByUuid(vehicle.getUuid());
        verify(vehicleMapper, never()).toDTO(any(Vehicle.class));
    }
}
