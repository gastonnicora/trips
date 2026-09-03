package com.gastonnicora.trips.unit.service.company;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
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
public class GetWorkersByCompanyTest {

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
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company.setUuid(UUID.randomUUID());

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(new CompanyDTO());

        when(workerService.getWorkersByCompany(company.getUuid())).thenReturn(new WorkersByCompany());

        WorkersByCompany result = companyService.getWorkersByCompany(company.getUuid());
        System.out.println(result);
        assertEquals(0, result.getWorkers().size());
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullywhenHaveWorkers() {
        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company.setUuid(UUID.randomUUID());

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(new CompanyDTO());

        when(workerService.getWorkersByCompany(company.getUuid())).thenReturn(new WorkersByCompany(new CompanyDTO(), List.of(new WorkerUser(), new WorkerUser())));

        WorkersByCompany result = companyService.getWorkersByCompany(company.getUuid());
        System.out.println(result);
        assertEquals(2, result.getWorkers().size());
    }

    @Test
    void shouldThrowBadRequestWhenCompanyNotFound() {

        when(companyRepository.findByUuid(any())).thenReturn(java.util.Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> companyService.getWorkersByCompany(UUID.randomUUID()));

        assertEquals("Empresa no encontrada", ex.getMessage());

        verify(companyRepository).findByUuid(any());

    }
}
