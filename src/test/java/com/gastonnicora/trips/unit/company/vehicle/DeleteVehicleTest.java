package com.gastonnicora.trips.unit.company.vehicle;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.VehicleService;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
public class DeleteVehicleTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void shouldDeleteVehicleSuccessfully() {
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

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        companyService.deleteVehicle(companyUuid, vehicleUuid);

        verify(vehicleService).deleteVehicle(vehicleUuid);
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.empty());

        try {
            companyService.deleteVehicle(companyUuid, vehicleUuid);
        } catch (RuntimeException e) {
            assertEquals("Empresa no encontrada", e.getMessage());
        }

        verify(vehicleService, never()).deleteVehicle(any());
        verify(companyRepository).findByUuid(companyUuid);
    }

    @Test
    void shouldPropagateExceptionWhenVehicleNotExists() {

        UUID companyUuid = UUID.randomUUID();
        UUID vehicleUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -34.3816,
                "company@mail.com",
                "123456789"
        );
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));
        doThrow(new NotFoundException("Vehicle no encontrado"))
                .when(vehicleService)
                .deleteVehicle(vehicleUuid);

        NotFoundException result = org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> companyService.deleteVehicle(companyUuid, vehicleUuid)
        );

        assertEquals(
                "Vehicle no encontrado",
                result.getMessage()
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(vehicleService)
                .deleteVehicle(vehicleUuid);
    }

}
