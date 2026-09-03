package com.gastonnicora.trips.unit.service.worker;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
public class UpdateWorkerTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldUpdateWorkerSuccessfully() {
        User user = new User();
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        Company company = new Company();
        UUID companyUuid = UUID.randomUUID();
        company.setUuid(companyUuid);

        Worker worker = new Worker(user, company, Set.of(RoleCompany.DRIVER));

        when(workerRepository.findByUserUuidAndCompanyUuid(userUuid, companyUuid))
                .thenReturn(java.util.Optional.of(worker));


        WorkerDTO expectedDTO = new WorkerDTO();

        when(workerMapper.toDTO(any())).thenReturn(expectedDTO);

        ArgumentCaptor<Worker> captor = ArgumentCaptor.forClass(Worker.class);

        workerService.updateWorker(userUuid, companyUuid, Set.of(RoleCompany.ADMIN));

        verify(workerRepository).save(worker);

        verify(workerRepository).save(captor.capture());

        Worker updated = captor.getValue();
        assertEquals(Set.of(RoleCompany.ADMIN), updated.getRoles());

        verify(workerRepository).findByUserUuidAndCompanyUuid(userUuid, companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenWorkerNotFound() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        when(workerRepository.findByUserUuidAndCompanyUuid(userUuid, companyUuid))
                .thenReturn(java.util.Optional.empty());

       assertThrows(NotFoundException.class, () -> {
            workerService.updateWorker(userUuid, companyUuid, Set.of(RoleCompany.ADMIN));
        });
    }

}
