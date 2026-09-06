package com.gastonnicora.trips.unit.user;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkerCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
public class GetWorkersByUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkerService workerService;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnWorkersByUser_whenUserExists() {
        UUID uuid = UUID.randomUUID();

        User user = new User("John", "Doe", "john.doe@example.com", "password",
                java.util.Set.of(com.gastonnicora.trips.enums.Role.USER));
        user.setUuid(uuid);

        WorkerCompany worker1 = new WorkerCompany();
        WorkerCompany worker2 = new WorkerCompany();
        WorkerCompany worker3 = new WorkerCompany();

        WorkersByUser listWorkersByUser = new WorkersByUser();

        listWorkersByUser.setUser(new UserDTO(user.getUuid(), user.getName(), user.getLastname(), user.getEmail(),
                user.getRole(), user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt()));

        listWorkersByUser.setWorkers(java.util.List.of(worker1, worker2, worker3));

        when(userRepository.findByUuid(uuid)).thenReturn(java.util.Optional.of(user));
        when(workerService.getWorkersByUser(uuid)).thenReturn(listWorkersByUser);
        WorkersByUser result = userService.getWorkersByUser(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUser().getUuid());
        assertEquals(3, result.getWorkers().size());
        verify(workerService).getWorkersByUser(uuid);
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void shouldThrowNotFound_whenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getWorkersByUser(uuid));
        verify(userRepository).findByUuid(uuid);
    }

    @Test
    void shouldReturnEmptyWorkersByUser_whenUserHasNoWorkers() {
        UUID uuid = UUID.randomUUID();
        User user = new User("John", "Doe", "john.doe@example.com", "password",
                java.util.Set.of(com.gastonnicora.trips.enums.Role.USER));
        user.setUuid(uuid);

        WorkersByUser emptyWorkersByUser = new WorkersByUser();
        emptyWorkersByUser.setUser(new UserDTO(user.getUuid(), user.getName(), user.getLastname(), user.getEmail(),
                user.getRole(), user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt()));
        emptyWorkersByUser.setWorkers(java.util.List.of());

        when(userRepository.findByUuid(uuid)).thenReturn(java.util.Optional.of(user));
        when(workerService.getWorkersByUser(uuid)).thenReturn(emptyWorkersByUser);

        WorkersByUser result = userService.getWorkersByUser(uuid);

        assertNotNull(result);
        assertEquals(uuid, result.getUser().getUuid());
        assertEquals(0, result.getWorkers().size());
        verify(workerService).getWorkersByUser(uuid);
    }
}
