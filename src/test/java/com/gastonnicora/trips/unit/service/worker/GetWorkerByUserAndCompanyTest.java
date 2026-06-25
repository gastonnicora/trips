package com.gastonnicora.trips.unit.service.worker;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
public class GetWorkerByUserAndCompanyTest {
    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldGetWorkerByUserAndCompanySuccessfully() {
        User user = new User();
        UUID user_uuid = UUID.randomUUID();
        user.setUuid(user_uuid);

        Company company = new Company();
        UUID company_uuid = UUID.randomUUID();
        company.setUuid(company_uuid);

        Worker worker = new Worker(user, company, Set.of(RoleCompany.DRIVER));

        when(workerRepository.findByUserUuidAndCompanyUuid(user_uuid, company_uuid)).thenReturn(Optional.of(worker));

        WorkerDTO expWorkerDTO = new WorkerDTO();
        when(workerMapper.toDTO(worker)).thenReturn(expWorkerDTO);

        WorkerDTO result = workerService.getWorkerByUserAndCompany(user_uuid, company_uuid);

        assertEquals(expWorkerDTO, result);

        verify(workerRepository).findByUserUuidAndCompanyUuid(user_uuid, company_uuid);
        verify(workerMapper).toDTO(worker);
    }

    @Test
    void shouldThrowNotFoundException_whenWorkerDoesNotExist() {
        when(workerRepository.findByUserUuidAndCompanyUuid(any(), any())).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            workerService.getWorkerByUserAndCompany(UUID.randomUUID(), UUID.randomUUID());
        });

        assertEquals(404, ex.getStatus());

        verify(workerRepository).findByUserUuidAndCompanyUuid(any(), any());

        verify(workerMapper, never()).toDTO(any(Worker.class));
    }

}
