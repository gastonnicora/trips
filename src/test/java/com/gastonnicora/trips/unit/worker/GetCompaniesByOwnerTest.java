package com.gastonnicora.trips.unit.worker;

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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompaniesByOwnerTest {

    @InjectMocks
    private WorkerService workerService;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private WorkerMapper workerMapper;

    @Test
    void shouldReturnCompaniesOwnedByUser() {
        UUID ownerUuid = UUID.randomUUID();

        User owner = new User();
        owner.setUuid(ownerUuid);

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        Company company2 = new Company();
        company2.setUuid(UUID.randomUUID());

        List<Worker> workers = List.of(
                new Worker(owner, company, Set.of(RoleCompany.OWNER)),
                new Worker(owner, company2, Set.of(RoleCompany.OWNER))
        );

        when(workerRepository.findAllByUserUuidAndRolesContainsAndActiveTrue(
                ownerUuid,
                RoleCompany.OWNER
        )).thenReturn(workers);

        List<Company> result = workerService.getCompaniesByOwner(ownerUuid);

        assertEquals(List.of(company, company2), result);

        verify(workerRepository)
                .findAllByUserUuidAndRolesContainsAndActiveTrue(ownerUuid, RoleCompany.OWNER);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerHasNoCompanies() {
        UUID ownerUuid = UUID.randomUUID();

        when(workerRepository.findAllByUserUuidAndRolesContainsAndActiveTrue(
                ownerUuid,
                RoleCompany.OWNER
        )).thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> workerService.getCompaniesByOwner(ownerUuid)
        );

        assertEquals(404, exception.getStatus());
        assertEquals("Empresas no encontradas", exception.getMessage());

        verify(workerRepository)
                .findAllByUserUuidAndRolesContainsAndActiveTrue(ownerUuid, RoleCompany.OWNER);
    }
}
