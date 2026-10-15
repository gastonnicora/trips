package com.gastonnicora.trips.unit.worker;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWorkerByUserAndCompanyTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldReturnWorkerWhenUserAndCompanyMatch() {
        User user = new User();
        UUID userUuid = UUID.randomUUID();
        user.setUuid(userUuid);

        Company company = new Company();
        UUID companyUuid = UUID.randomUUID();
        company.setUuid(companyUuid);

        Worker worker = new Worker(
                user,
                company,
                Set.of(RoleCompany.DRIVER)
        );

        WorkerDTO expectedWorkerDTO = new WorkerDTO();

        when(workerRepository.findByUserUuidAndCompanyUuidAndActiveTrue(
                userUuid,
                companyUuid
        )).thenReturn(Optional.of(worker));

        when(workerMapper.toDTO(worker))
                .thenReturn(expectedWorkerDTO);

        WorkerDTO result
                = workerService.getWorkerByUserAndCompany(
                userUuid,
                companyUuid
        );

        assertEquals(expectedWorkerDTO, result);

        verify(workerRepository)
                .findByUserUuidAndCompanyUuidAndActiveTrue(userUuid, companyUuid);

        verify(workerMapper)
                .toDTO(worker);
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
                () -> workerService.getWorkerByUserAndCompany(
                        userUuid,
                        companyUuid
                )
        );

        assertEquals(404, exception.getStatus());

        verify(workerRepository)
                .findByUserUuidAndCompanyUuidAndActiveTrue(userUuid, companyUuid);

        verify(workerMapper, never())
                .toDTO(any(Worker.class));
    }
}
