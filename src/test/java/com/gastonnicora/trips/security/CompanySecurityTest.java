package com.gastonnicora.trips.security;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.repositories.WorkerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanySecurityTest {

    @Mock
    private WorkerRepository workerRepository;

    private CompanySecurity companySecurity;
    private UUID userUuid;
    private UUID companyUuid;

    @Test
    void roleConvenienceMethodsShouldDelegateToRoleChecks() {
        Worker worker = workerWithRoles(RoleCompany.SELLER, RoleCompany.DRIVER);
        when(workerRepository.findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.of(worker));

        assertTrue(companySecurity.isSeller(companyUuid));
        assertTrue(companySecurity.isDriver(companyUuid));
        assertFalse(companySecurity.isAdmin(companyUuid));
        assertFalse(companySecurity.isHrManager(companyUuid));
    }

    @BeforeEach
    void setUp() {
        companySecurity = new CompanySecurity(workerRepository);
        userUuid = UUID.randomUUID();
        companyUuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(userUuid);
        user.setEmail("user@test.com");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new UserDetailsImpl(user), null, Set.of()));
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotHaveRequestedRole() {
        Worker worker = workerWithRoles(RoleCompany.SELLER);
        when(workerRepository.findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.of(worker));

        assertFalse(companySecurity.hasRole(companyUuid, RoleCompany.ADMIN));
    }

    @Test
    void shouldReturnFalseWhenUserIsNotWorkerOfCompany() {
        when(workerRepository.findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertFalse(companySecurity.hasRole(companyUuid, RoleCompany.ADMIN));
        assertFalse(companySecurity.hasAnyRole(companyUuid, RoleCompany.ADMIN, RoleCompany.OWNER));
    }

    @Test
    void shouldReturnTrueWhenUserHasAnyRequestedRole() {
        Worker worker = workerWithRoles(RoleCompany.HR_MANAGER);
        when(workerRepository.findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.of(worker));

        assertTrue(companySecurity.hasAnyRole(
                companyUuid,
                RoleCompany.ADMIN,
                RoleCompany.HR_MANAGER));
    }

    @Test
    void shouldReturnTrueWhenUserHasRequestedRole() {
        Worker worker = workerWithRoles(RoleCompany.ADMIN);
        when(workerRepository.findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid))
                .thenReturn(Optional.of(worker));

        assertTrue(companySecurity.hasRole(companyUuid, RoleCompany.ADMIN));
        verify(workerRepository).findByUserUuidAndCompanyUuidAndUserEnabledTrueAndCompanyActiveTrue(userUuid, companyUuid);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Worker workerWithRoles(RoleCompany... roles) {
        User user = new User();
        user.setUuid(userUuid);
        Company company = new Company();
        company.setUuid(companyUuid);
        return new Worker(user, company, Set.of(roles));
    }
}
