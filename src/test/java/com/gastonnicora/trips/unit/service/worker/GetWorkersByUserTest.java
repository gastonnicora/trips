package com.gastonnicora.trips.unit.service.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
public class GetWorkersByUserTest {
    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldGetWorkersByUserSuccessfully() {
        User user = new User();
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Company company2 = new Company();
        company2.setUuid(UUID.randomUUID());

        List<Worker> workers = List.of(new Worker(user, company, Set.of(RoleCompany.DRIVER)),
                new Worker(user, company2, Set.of(RoleCompany.OWNER)));

        when(workerRepository.findAllByUserUuid(userUuid)).thenReturn(workers);

        WorkersByUser expected = new WorkersByUser();

        when(workerMapper.toWorkersByUserDTO(workers)).thenReturn(expected);

        WorkersByUser result = workerService.getWorkersByUser(userUuid);

        assertEquals(expected, result);

        verify(workerRepository).findAllByUserUuid(userUuid);

        verify(workerMapper).toWorkersByUserDTO(workers);
    }
}
