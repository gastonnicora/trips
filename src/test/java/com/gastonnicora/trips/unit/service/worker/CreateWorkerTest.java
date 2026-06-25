package com.gastonnicora.trips.unit.service.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class CreateWorkerTest {
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

        Worker worker = new Worker();

        WorkerDTO expectedDTO = new WorkerDTO();

        when(workerRepository.save(any())).thenReturn(worker);
        when(workerMapper.toDTO(any())).thenReturn(expectedDTO);

        WorkerDTO result = workerService.createWorker(user, company, Set.of(RoleCompany.DRIVER));

        assertEquals(expectedDTO, result);

        verify(workerRepository).save(any(Worker.class));
        verify(workerMapper).toDTO(worker);
    }

}
