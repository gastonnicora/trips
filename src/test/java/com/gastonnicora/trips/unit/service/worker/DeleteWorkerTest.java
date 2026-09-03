package com.gastonnicora.trips.unit.service.worker;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

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

        when(workerRepository.findByUserUuidAndCompanyUuid(
                userUuid,
                companyUuid
        )).thenReturn(Optional.of(worker));

        workerService.deleteWorker(userUuid, companyUuid);

        verify(workerRepository)
                .findByUserUuidAndCompanyUuid(userUuid, companyUuid);

        ArgumentCaptor<Worker> captor =
                ArgumentCaptor.forClass(Worker.class);

        verify(workerRepository).save(captor.capture());

        Worker deleted = captor.getValue();

        assertEquals(worker, deleted);
        assertFalse(deleted.isActive());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenWorkerDoesNotExist() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        when(workerRepository.findByUserUuidAndCompanyUuid(
                userUuid,
                companyUuid
        )).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> workerService.deleteWorker(userUuid, companyUuid)
        );

        assertEquals(404, exception.getStatus());

        verify(workerRepository)
                .findByUserUuidAndCompanyUuid(userUuid, companyUuid);

        verify(workerRepository, never())
                .save(any(Worker.class));
    }
}
