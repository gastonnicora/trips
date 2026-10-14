package com.gastonnicora.trips.unit.worker;

import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWorkersByUserTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldReturnWorkersWhenUserExists() {
        User user = new User();
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Company company2 = new Company();
        company2.setUuid(UUID.randomUUID());

        List<Worker> workers = List.of(
                new Worker(user, company, Set.of(RoleCompany.DRIVER)),
                new Worker(user, company2, Set.of(RoleCompany.OWNER))
        );

        WorkersByUser expected = new WorkersByUser();

        when(workerRepository.findAllByUserUuidAndActiveTrue(userUuid))
                .thenReturn(workers);

        when(workerMapper.toWorkersByUserDTO(workers))
                .thenReturn(expected);

        WorkersByUser result
                = workerService.getWorkersByUser(userUuid);

        assertEquals(expected, result);

        verify(workerRepository)
                .findAllByUserUuidAndActiveTrue(userUuid);

        verify(workerMapper)
                .toWorkersByUserDTO(workers);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserHasNoWorkers() {
        UUID userUuid = UUID.randomUUID();

        when(workerRepository.findAllByUserUuidAndActiveTrue(userUuid))
                .thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> workerService.getWorkersByUser(userUuid)
        );

        assertEquals(404, exception.getStatus());
        assertEquals("El usuario no tiene trabajos", exception.getMessage());

        verify(workerRepository).findAllByUserUuidAndActiveTrue(userUuid);
        verify(workerMapper, never()).toWorkersByUserDTO(List.of());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenWorkersIsNull() {
        UUID userUuid = UUID.randomUUID();

        when(workerRepository.findAllByUserUuidAndActiveTrue(userUuid))
                .thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> workerService.getWorkersByUser(userUuid)
        );

        assertEquals(404, exception.getStatus());
        assertEquals("El usuario no tiene trabajos", exception.getMessage());

        verify(workerRepository).findAllByUserUuidAndActiveTrue(userUuid);
        verify(workerMapper, never()).toWorkersByUserDTO(List.of());
    }
}
