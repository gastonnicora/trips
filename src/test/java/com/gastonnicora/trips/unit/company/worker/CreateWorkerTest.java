package com.gastonnicora.trips.unit.company.worker;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkerUser;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class CreateWorkerTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserService userService;

    @Mock
    private WorkerService workerService;

    @Test
    void shouldCreateWorkerSuccessfully() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        Set<RoleCompany> roles = Set.of(RoleCompany.DRIVER);

        WorkerDTO expected = new WorkerDTO();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(new WorkersByCompany());

        when(workerService.createWorker(user, company, roles))
                .thenReturn(expected);

        WorkerDTO result = companyService.createWorker(
                userUuid,
                companyUuid,
                roles
        );

        assertEquals(expected, result);

        verify(companyRepository).findByUuid(companyUuid);
        verify(userService).getUser(userUuid);
        verify(workerService).getWorkersByCompany(companyUuid);
        verify(workerService).createWorker(user, company, roles);
    }

    @Test
    void shouldThrowConflictExceptionWhenUserIsAlreadyWorker() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        UserDTO user = new UserDTO();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        WorkerUser worker = new WorkerUser(
                UUID.randomUUID(),
                user,
                Set.of(RoleCompany.DRIVER),
                true
        );

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(new User());

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(new WorkersByCompany(
                        new CompanyDTO(),
                        List.of(worker)
                ));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> companyService.createWorker(
                        userUuid,
                        companyUuid,
                        Set.of(RoleCompany.DRIVER)
                )
        );

        assertEquals(
                "El usuario ya es trabajador de la empresa",
                exception.getMessage()
        );

        verify(workerService, never())
                .createWorker(any(), any(), any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreNull() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(new WorkersByCompany());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.createWorker(
                        userUuid,
                        companyUuid,
                        null
                )
        );

        assertEquals(
                "Se debe asignar al menos un rol al trabajador",
                exception.getMessage()
        );

        verify(workerService, never())
                .createWorker(any(), any(), any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreEmpty() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(new WorkersByCompany());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.createWorker(
                        userUuid,
                        companyUuid,
                        Set.of()
                )
        );

        assertEquals(
                "Se debe asignar al menos un rol al trabajador",
                exception.getMessage()
        );

        verify(workerService, never())
                .createWorker(any(), any(), any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRoleIsOwner() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(java.util.Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.getWorkersByCompany(companyUuid))
                .thenReturn(new WorkersByCompany());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.createWorker(
                        userUuid,
                        companyUuid,
                        Set.of(RoleCompany.OWNER)
                )
        );

        assertEquals(
                "No se puede asignar el rol de OWNER a un trabajador",
                exception.getMessage()
        );

        verify(workerService, never())
                .createWorker(any(), any(), any());
    }
}
