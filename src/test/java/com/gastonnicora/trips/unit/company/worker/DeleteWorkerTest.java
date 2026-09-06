package com.gastonnicora.trips.unit.company.worker;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class DeleteWorkerTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserService userService;

    @Mock
    private WorkerService workerService;

    @Test
    void shouldDeleteWorkerSuccessfully() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        companyService.deleteWorker(userUuid, companyUuid);

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(userService)
                .getUser(userUuid);

        verify(workerService)
                .deleteWorker(userUuid, companyUuid);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyDoesNotExist() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> companyService.deleteWorker(
                        userUuid,
                        companyUuid
                )
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(userService, never())
                .getUser(userUuid);

        verify(workerService, never())
                .deleteWorker(userUuid, companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(userService.getUser(userUuid))
                .thenThrow(new NotFoundException("Usuario no encontrado"));

        assertThrows(
                NotFoundException.class,
                () -> companyService.deleteWorker(
                        userUuid,
                        companyUuid
                )
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(userService)
                .getUser(userUuid);

        verify(workerService, never())
                .deleteWorker(userUuid, companyUuid);
    }
}
