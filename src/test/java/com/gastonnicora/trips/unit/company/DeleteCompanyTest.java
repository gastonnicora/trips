package com.gastonnicora.trips.unit.company;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
public class DeleteCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

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

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));

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
        company.setUuid(UUID.randomUUID());
        company.setActive(true);

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> companyService.deleteCompany(company.getUuid()));
        verify(companyRepository).findByUuid(company.getUuid());

    }

}
