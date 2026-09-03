package com.gastonnicora.trips.unit.service.worker;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class CreateWorkerTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldCreateWorkerSuccessfully() {
        User user = new User();
        user.setUuid(UUID.randomUUID());

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Set<RoleCompany> roles = Set.of(RoleCompany.DRIVER);

        Worker savedWorker = new Worker(user, company, roles);
        WorkerDTO expectedDTO = new WorkerDTO();

        when(workerRepository.save(any(Worker.class)))
                .thenReturn(savedWorker);

        when(workerMapper.toDTO(savedWorker))
                .thenReturn(expectedDTO);

        WorkerDTO result = workerService.createWorker(
                user,
                company,
                roles
        );

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<Worker> captor
                = ArgumentCaptor.forClass(Worker.class);

        verify(workerRepository).save(captor.capture());

        Worker workerSaved = captor.getValue();

        assertEquals(user, workerSaved.getUser());
        assertEquals(company, workerSaved.getCompany());
        assertEquals(roles, workerSaved.getRoles());

        verify(workerMapper).toDTO(savedWorker);
    }
}
