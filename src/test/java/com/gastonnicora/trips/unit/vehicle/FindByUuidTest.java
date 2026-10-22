package com.gastonnicora.trips.unit.vehicle;

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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        Company company = new Company();
        company.setUuid(UUID.randomUUID());
        company.setActive(true);
        vehicle.setCompany(company);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid()))
                .thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findByUuid(company.getUuid(), uuid);

        assertEquals(vehicle, result);
        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyIsInactive() {
        UUID uuid = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setUuid(uuid);
        vehicle.setActive(true);
        Company company = new Company();
        company.setUuid(UUID.randomUUID());
        company.setActive(false);
        vehicle.setCompany(company);

        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> vehicleService.findByUuid(company.getUuid(), uuid)
                                                  );

        assertEquals(404, exception.getStatus());
        assertEquals("Vehículo no encontrado", exception.getMessage());
        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenVehicleDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(UUID.randomUUID());
        company.setActive(true);
        when(vehicleRepository.findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> vehicleService.findByUuid(company.getUuid(), uuid)
                                                  );

        assertEquals(404, exception.getStatus());
        assertEquals("Vehículo no encontrado", exception.getMessage());
        verify(vehicleRepository).findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(uuid, company.getUuid());
    }
}
