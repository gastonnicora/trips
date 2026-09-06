package com.gastonnicora.trips.unit.vehicle;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.services.VehicleService;

@ExtendWith(MockitoExtension.class)
class CreateVehicleTest {

    @InjectMocks
    private VehicleService vehicleService;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Test
    void shouldCreateVehicleSuccessfully() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        VehicleCreate vehicleCreate = new VehicleCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        Vehicle vehicle = new Vehicle(
                company,
                vehicleCreate.getPlate(),
                vehicleCreate.getModel(),
                vehicleCreate.getCapacity()
        );
        vehicle.setUuid(vehicleUuid);

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setUuid(vehicleUuid);

        when(vehicleRepository.findByCompanyUuidAndPlate(
                companyUuid,
                vehicleCreate.getPlate()
        )).thenReturn(Optional.empty());

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(vehicle);

        when(vehicleMapper.toDTO(vehicle))
                .thenReturn(expectedDTO);

        VehicleDTO result = vehicleService.createVehicle(
                company,
                vehicleCreate
        );

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(vehicleRepository)
                .findByCompanyUuidAndPlate(
                        companyUuid,
                        vehicleCreate.getPlate()
                );

        ArgumentCaptor<Vehicle> captor
                = ArgumentCaptor.forClass(Vehicle.class);

        verify(vehicleRepository).save(captor.capture());

        Vehicle saved = captor.getValue();

        assertEquals(company, saved.getCompany());
        assertEquals(vehicleCreate.getPlate(), saved.getPlate());
        assertEquals(vehicleCreate.getModel(), saved.getModel());
        assertEquals(vehicleCreate.getCapacity(), saved.getCapacity());
        assertTrue(saved.isActive());

        verify(vehicleMapper)
                .toDTO(vehicle);
    }

    @Test
    void shouldThrowConflictWhenPlateAlreadyExists() {

        UUID companyUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        VehicleCreate vehicleCreate = new VehicleCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        Vehicle existingVehicle = new Vehicle(
                company,
                vehicleCreate.getPlate(),
                vehicleCreate.getModel(),
                vehicleCreate.getCapacity()
        );

        when(vehicleRepository.findByCompanyUuidAndPlate(
                companyUuid,
                vehicleCreate.getPlate()
        )).thenReturn(Optional.of(existingVehicle));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> vehicleService.createVehicle(
                        company,
                        vehicleCreate
                )
        );

        assertEquals(
                "Ya existe un vehicle con la misma patente para esta empresa",
                exception.getMessage()
        );

        verify(vehicleRepository)
                .findByCompanyUuidAndPlate(
                        companyUuid,
                        vehicleCreate.getPlate()
                );
        verify(vehicleRepository, never())
                .save(any(Vehicle.class));

        verify(vehicleMapper, never())
                .toDTO(any(Vehicle.class));
    }

}
