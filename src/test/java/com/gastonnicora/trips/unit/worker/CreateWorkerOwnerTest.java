package com.gastonnicora.trips.unit.worker;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkerOwnerTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldCreateWorkerOwnerSuccessfully() {
        User user = new User();
        user.setUuid(UUID.randomUUID());

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Worker savedWorker = new Worker(user, company, Set.of(RoleCompany.OWNER));
        WorkerDTO expectedDTO = new WorkerDTO();

        when(workerRepository.findAllByCompanyUuidAndActiveTrue(company.getUuid()))
                .thenReturn(List.of());
        when(workerRepository.save(any(Worker.class)))
                .thenReturn(savedWorker);
        when(workerMapper.toDTO(savedWorker))
                .thenReturn(expectedDTO);

        WorkerDTO result = workerService.createWorkerOwner(user, company);

        assertNotNull(result);
        assertEquals(expectedDTO, result);

        ArgumentCaptor<Worker> captor
                = ArgumentCaptor.forClass(Worker.class);

        verify(workerRepository)
                .findAllByCompanyUuidAndActiveTrue(company.getUuid());
        verify(workerRepository).save(captor.capture());

        Worker workerSaved = captor.getValue();

        assertEquals(user, workerSaved.getUser());
        assertEquals(company, workerSaved.getCompany());
        assertEquals(Set.of(RoleCompany.OWNER), workerSaved.getRoles());

        verify(workerMapper).toDTO(savedWorker);
    }

    @Test
    void shouldThrowConflictExceptionWhenCompanyAlreadyHasWorkers() {
        User user = new User();
        user.setUuid(UUID.randomUUID());

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Worker worker = new Worker(user, company, Set.of(RoleCompany.ADMIN));

        when(workerRepository.findAllByCompanyUuidAndActiveTrue(company.getUuid()))
                .thenReturn(List.of(worker));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> workerService.createWorkerOwner(user, company));

        assertEquals(409, exception.getStatus());
        assertEquals("Ya existen trabajadores", exception.getMessage());

        verify(workerRepository)
                .findAllByCompanyUuidAndActiveTrue(company.getUuid());
        verify(workerRepository, never()).save(any(Worker.class));
        verify(workerMapper, never()).toDTO(any(Worker.class));
    }
}
