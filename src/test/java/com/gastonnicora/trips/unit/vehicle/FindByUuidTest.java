package com.gastonnicora.trips.unit.vehicle;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;

@ExtendWith(MockitoExtension.class)
class FindByUuidTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void shouldReturnVehicleWhenUuidExists() {
        UUID uuid = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setUuid(uuid);
        vehicle.setActive(true);

        when(vehicleRepository.findByUuidAndActiveTrue(uuid))
                .thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findByUuid(uuid);

        assertEquals(vehicle, result);
        verify(vehicleRepository).findByUuidAndActiveTrue(uuid);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenVehicleDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(vehicleRepository.findByUuidAndActiveTrue(uuid))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> vehicleService.findByUuid(uuid));

        assertEquals(404, exception.getStatus());
        assertEquals("Vehículo no encontrado", exception.getMessage());
        verify(vehicleRepository).findByUuidAndActiveTrue(uuid);
    }
}
