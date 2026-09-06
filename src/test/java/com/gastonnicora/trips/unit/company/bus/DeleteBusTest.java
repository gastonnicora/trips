package com.gastonnicora.trips.unit.company.bus;

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
import com.gastonnicora.trips.services.BusService;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
public class DeleteBusTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private BusService busService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void shouldDeleteBusSuccessfully() {
        UUID companyUuid = UUID.randomUUID();
        UUID busUuid = UUID.randomUUID();

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

        companyService.deleteBus(companyUuid, busUuid);

        verify(busService).deleteBus(busUuid);
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyUuid = UUID.randomUUID();
        UUID busUuid = UUID.randomUUID();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.empty());

        try {
            companyService.deleteBus(companyUuid, busUuid);
        } catch (RuntimeException e) {
            assertEquals("Empresa no encontrada", e.getMessage());
        }

        verify(busService, never()).deleteBus(any());
        verify(companyRepository).findByUuid(companyUuid);
    }

    @Test
    void shouldPropagateExceptionWhenBusNotExists() {

        UUID companyUuid = UUID.randomUUID();
        UUID busUuid = UUID.randomUUID();

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
        doThrow(new NotFoundException("Bus no encontrado"))
                .when(busService)
                .deleteBus(busUuid);

        NotFoundException result = org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> companyService.deleteBus(companyUuid, busUuid)
        );

        assertEquals(
                "Bus no encontrado",
                result.getMessage()
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(busService)
                .deleteBus(busUuid);
    }

}
