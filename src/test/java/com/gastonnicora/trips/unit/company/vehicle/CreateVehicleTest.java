package com.gastonnicora.trips.unit.company.vehicle;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.VehicleService;

@ExtendWith(MockitoExtension.class)
class CreateVehicleTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private CompanyRepository companyRepository;

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

        VehicleDTO expectedDTO = new VehicleDTO();
        expectedDTO.setUuid(vehicleUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(vehicleService.createVehicle(company, vehicleCreate))
                .thenReturn(expectedDTO);

        VehicleDTO result = companyService.createVehicle(
                companyUuid,
                vehicleCreate
        );

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(vehicleService)
                .createVehicle(company, vehicleCreate);
        verify(companyRepository)
                .findByUuid(companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyUuid = UUID.randomUUID();
        VehicleCreate vehicleCreate = new VehicleCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.empty());

        try {
            companyService.createVehicle(companyUuid, vehicleCreate);
        } catch (RuntimeException e) {
            assertEquals("Empresa no encontrada", e.getMessage());
        }

        verify(vehicleService, never()).createVehicle(any(), any());
        verify(companyRepository).findByUuid(companyUuid);
    }

    @Test
    void shouldPropagateExceptionWhenVehicleAlreadyExists() {

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

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        ConflictException exception = new ConflictException(
                "Ya existe un vehículo con la misma patente para esta empresa"
        );

        when(vehicleService.createVehicle(company, vehicleCreate))
                .thenThrow(exception);

        ConflictException result = org.junit.jupiter.api.Assertions.assertThrows(
                ConflictException.class,
                () -> companyService.createVehicle(companyUuid, vehicleCreate)
        );

        assertEquals(
                "Ya existe un vehículo con la misma patente para esta empresa",
                result.getMessage()
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(vehicleService)
                .createVehicle(company, vehicleCreate);
    }
}
