package com.gastonnicora.trips.unit.service.company;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.UnauthorizedException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
public class DeleteCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldDeleteCompanySuccessfully() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setUuid(userId);

        Company company = new Company();
        company.setUuid(UUID.randomUUID());
        company.setActive(true);
        company.setOwner(user);

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));

        context(userId);
        companyService.deleteCompany(company.getUuid());

        verify(companyRepository).findByUuid(company.getUuid());
        verify(companyRepository).save(company);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyNotFound() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUuid(userId);

        Company company = new Company();
        company.setOwner(user);
        company.setUuid(UUID.randomUUID());
        company.setActive(true);

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> companyService.deleteCompany(company.getUuid()));
        verify(companyRepository).findByUuid(company.getUuid());

    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUserNotOwner() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUuid(userId);

        Company company = new Company();
        company.setOwner(user);
        company.setUuid(UUID.randomUUID());
        company.setActive(true);
        context(UUID.randomUUID());
        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));

        assertThrows(UnauthorizedException.class, () -> companyService.deleteCompany(company.getUuid()));

        verify(companyRepository).findByUuid(company.getUuid());
    }

    private void context(UUID uuid) {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUuid()).thenReturn(uuid);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }
}
