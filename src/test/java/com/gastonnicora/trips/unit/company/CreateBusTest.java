package com.gastonnicora.trips.unit.company;

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

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.dtos.request.bus.BusCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.BusService;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
class CreateBusTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private BusService busService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    void shouldCreateBusSuccessfully() {

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

        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        BusDTO expectedDTO = new BusDTO();
        expectedDTO.setUuid(busUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(busService.createBus(company, busCreate))
                .thenReturn(expectedDTO);

        BusDTO result = companyService.createBus(
                companyUuid,
                busCreate
        );

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        verify(busService)
                .createBus(company, busCreate);
        verify(companyRepository)
                .findByUuid(companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenCompanyNotFound() {
        UUID companyUuid = UUID.randomUUID();
        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.empty());

        try {
            companyService.createBus(companyUuid, busCreate);
        } catch (RuntimeException e) {
            assertEquals("Empresa no encontrada", e.getMessage());
        }

        verify(busService, never()).createBus(any(), any());
        verify(companyRepository).findByUuid(companyUuid);
    }

    @Test
    void shouldPropagateExceptionWhenBusAlreadyExists() {

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

        BusCreate busCreate = new BusCreate(
                "AA123BB",
                "Mercedes Benz",
                50
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        ConflictException exception = new ConflictException(
                "Ya existe un bus con la misma placa para esta empresa"
        );

        when(busService.createBus(company, busCreate))
                .thenThrow(exception);

        ConflictException result = org.junit.jupiter.api.Assertions.assertThrows(
                ConflictException.class,
                () -> companyService.createBus(companyUuid, busCreate)
        );

        assertEquals(
                "Ya existe un bus con la misma placa para esta empresa",
                result.getMessage()
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(busService)
                .createBus(company, busCreate);
    }
}
