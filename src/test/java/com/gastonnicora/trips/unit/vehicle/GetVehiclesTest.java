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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetVehiclesTest {


    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void shouldReturnVehiclesSuccessfully() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setActive(true);
        vehicle.setCompany(company);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setActive(true);
        vehicle2.setCompany(company);

        List<Vehicle> vehicles = List.of(vehicle, vehicle2);

        when(vehicleRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid())).thenReturn(vehicles);

        List<VehicleDTO> expectedVehicleDTO = List.of(new VehicleDTO(), new VehicleDTO());

        when(vehicleMapper.toDTOList(vehicles)).thenReturn(expectedVehicleDTO);

        List<VehicleDTO> result = vehicleService.getVehicles(company.getUuid());

        verify(vehicleRepository).findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());

        assertEquals(expectedVehicleDTO, result);

        verify(vehicleMapper).toDTOList(vehicles);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyDontHaveVehicle() {
        Company company = new Company();
        company.setUuid(java.util.UUID.randomUUID());
        Vehicle vehicle = new Vehicle();
        vehicle.setCompany(company);
        vehicle.setActive(false);
        when(vehicleRepository.findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid())).thenReturn(List.of());
        assertThrows(NotFoundException.class, () -> vehicleService.getVehicles(company.getUuid()));
        verify(vehicleRepository).findAllByCompanyUuidAndCompanyActiveTrueAndActiveTrue(company.getUuid());
        verify(vehicleMapper, never()).toDTOList(any());
    }
}
