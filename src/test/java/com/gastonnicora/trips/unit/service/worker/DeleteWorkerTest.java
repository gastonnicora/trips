package com.gastonnicora.trips.unit.service.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
public class DeleteWorkerTest {
    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Test
    void shouldDeleteWorkerSuccessfully() {
        User user = new User();
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        Company company = new Company();
        UUID companyUuid = UUID.randomUUID();
        company.setUuid(companyUuid);

        Worker worker = new Worker(user, company, Set.of(RoleCompany.DRIVER));

        when(workerRepository.findByUserUuidAndCompanyUuid(userUuid, companyUuid)).thenReturn(Optional.of(worker));

        ArgumentCaptor<Worker> captor = ArgumentCaptor.forClass(Worker.class);

        workerService.deleteWorker(userUuid, companyUuid);
        
        verify(workerRepository).save(captor.capture());
        Worker deleted = captor.getValue();

        assertFalse(deleted.isActive());
    }

    @Test
    void shouldThrowNotFoundException_whenWorkerDoesNotExist() {
        when(workerRepository.findByUserUuidAndCompanyUuid(any(), any())).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            workerService.deleteWorker(UUID.randomUUID(), UUID.randomUUID());
        });

        assertEquals(404, ex.getStatus());

        verify(workerRepository).findByUserUuidAndCompanyUuid(any(), any());

        verify(workerRepository, never()).save(any(Worker.class));
    }
}
