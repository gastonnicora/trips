package com.gastonnicora.trips.unit.worker;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteWorkerTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Test
    void shouldDeleteWorkerSuccessfully() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        Worker worker = new Worker(
                user,
                company,
                Set.of(RoleCompany.DRIVER)
        );

        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrue(
                userUuid,
                companyUuid
        )).thenReturn(Optional.of(worker));

        workerService.deleteWorker(userUuid, companyUuid);

        verify(workerRepository)
                .findByUserUuidAndCompanyUuidAndActiveTrue(userUuid, companyUuid);

        ArgumentCaptor<Worker> captor
                = ArgumentCaptor.forClass(Worker.class);

        verify(workerRepository).save(captor.capture());

        Worker deleted = captor.getValue();

        assertEquals(worker, deleted);
        assertFalse(deleted.isActive());
    }

    @Test
    void shouldThrowForbiddenExceptionWhenWorkerIsOwner() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        Worker owner = new Worker(user, company, Set.of(RoleCompany.OWNER));

        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.of(owner));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> workerService.deleteWorker(userUuid, companyUuid)
        );

        assertEquals(403, exception.getStatus());
        assertTrue(owner.isActive());
        verify(workerRepository, never()).save(any(Worker.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenWorkerDoesNotExist() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrue(
                userUuid,
                companyUuid
        )).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> workerService.deleteWorker(userUuid, companyUuid)
        );

        assertEquals(404, exception.getStatus());

        verify(workerRepository)
                .findByUserUuidAndCompanyUuidAndActiveTrue(userUuid, companyUuid);

        verify(workerRepository, never())
                .save(any(Worker.class));
    }
}
