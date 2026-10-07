package com.gastonnicora.trips.unit.worker;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class GetWorkersByUserNotFoundTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

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
        verify(workerMapper, never()).toWorkersByUserDTO(List.<Worker>of());
    }
}
