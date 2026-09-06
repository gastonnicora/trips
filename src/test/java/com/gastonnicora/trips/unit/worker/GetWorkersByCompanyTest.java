package com.gastonnicora.trips.unit.worker;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class GetWorkersByCompanyTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldGetWorkersByCompanySuccessfully() {
        User driver = new User();
        driver.setUuid(UUID.randomUUID());

        User owner = new User();
        owner.setUuid(UUID.randomUUID());

        UUID companyUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        List<Worker> workers = List.of(
                new Worker(driver, company, Set.of(RoleCompany.DRIVER)),
                new Worker(owner, company, Set.of(RoleCompany.OWNER))
        );

        WorkersByCompany expected = new WorkersByCompany();

        when(workerRepository.findAllByCompanyUuid(companyUuid))
                .thenReturn(workers);

        when(workerMapper.toWorkersByCompanyDTO(workers))
                .thenReturn(expected);

        WorkersByCompany result
                = workerService.getWorkersByCompany(companyUuid);

        assertEquals(expected, result);

        verify(workerRepository)
                .findAllByCompanyUuid(companyUuid);

        verify(workerMapper)
                .toWorkersByCompanyDTO(workers);
    }
}
