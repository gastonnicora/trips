package com.gastonnicora.trips.unit.company;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class UpdateWorkerTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserService userService;

    @Mock
    private WorkerService workerService;

    @Test
    void shouldUpdateWorkerSuccessfully() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);

        Company company = new Company();
        company.setUuid(companyUuid);

        Set<RoleCompany> roles = Set.of(RoleCompany.DRIVER);
        WorkerDTO expected = new WorkerDTO();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.updateWorker(userUuid, companyUuid, roles))
                .thenReturn(expected);

        WorkerDTO result = companyService.updateWorker(
                userUuid,
                companyUuid,
                roles
        );

        assertEquals(expected, result);

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(userService)
                .getUser(userUuid);

        verify(workerService)
                .updateWorker(userUuid, companyUuid, roles);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreNull() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.updateWorker(
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
                .updateWorker(userUuid, companyUuid, null);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreEmpty() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.updateWorker(
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
                .updateWorker(userUuid, companyUuid, Set.of()
                );
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRoleIsOwner() {
        UUID userUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();

        Company company = new Company();
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> companyService.updateWorker(
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
                .updateWorker(
                        userUuid,
                        companyUuid,
                        Set.of(RoleCompany.OWNER)
                );
    }
}
