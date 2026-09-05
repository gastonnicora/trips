package com.gastonnicora.trips.unit.company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkerUser;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class GetWorkersByCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private WorkerService workerService;

    @Mock
    private CompanyMapper companyMapper;

    @Test
    void shouldGetWorkersByCompanySuccessfully() {

        // Arrange
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company(
                "Test",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "test@mail.com",
                "123"
        );
        company.setUuid(companyUuid);

        CompanyDTO companyDTO = new CompanyDTO();
        WorkersByCompany expected = new WorkersByCompany(
                companyDTO,
                List.of()
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(companyMapper.toDTO(company))
                .thenReturn(companyDTO);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(expected);

        // Act
        WorkersByCompany result
                = companyService.getWorkersByCompany(companyUuid);

        // Assert
        assertSame(expected, result);

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(workerService)
                .getWorkersByCompany(companyUuid);
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyWhenCompanyHasWorkers() {

        // Arrange
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company(
                "Test",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "test@mail.com",
                "123"
        );
        company.setUuid(companyUuid);

        CompanyDTO companyDTO = new CompanyDTO();

        WorkerUser worker1 = new WorkerUser();
        WorkerUser worker2 = new WorkerUser();

        WorkersByCompany expected = new WorkersByCompany(
                companyDTO,
                List.of(worker1, worker2)
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(companyMapper.toDTO(company))
                .thenReturn(companyDTO);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(expected);

        // Act
        WorkersByCompany result
                = companyService.getWorkersByCompany(companyUuid);

        // Assert
        assertSame(expected, result);
        assertEquals(2, result.getWorkers().size());

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(workerService)
                .getWorkersByCompany(companyUuid);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyDoesNotExist() {

        // Arrange
        UUID companyUuid = UUID.randomUUID();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> companyService.getWorkersByCompany(companyUuid)
        );

        assertEquals(
                "Empresa no encontrada",
                exception.getMessage()
        );

        verify(companyRepository)
                .findByUuid(companyUuid);
    }
}
