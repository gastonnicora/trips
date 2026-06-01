package com.gastonnicora.trips.unit.service.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;

import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import com.gastonnicora.trips.services.UserService;

import jakarta.inject.Inject;

@ExtendWith(MockitoExtension.class)
public class GetCompaniesBYCurrentUserTest {
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
    void shouldGetCompaniesByCurrentUserSuccessfully() {
        UUID userId = UUID.randomUUID();
        context(userId);

        Company company = new Company();
        java.util.List<Company> companies = java.util.List.of(company);
        java.util.List<CompanyDTO> expectedDTOs = java.util.List.of(new CompanyDTO());

        when(companyRepository.findAllByOwner_Uuid(userId)).thenReturn(companies);
        when(companyMapper.toDTOList(companies)).thenReturn(expectedDTOs);

        com.gastonnicora.trips.dtos.response.ListResponse<CompanyDTO> result = companyService
                .getCompaniesByCurrentUser();

        assertEquals(expectedDTOs, result.getData());
        assertEquals(1, result.getTotal());

        verify(companyRepository).findAllByOwner_Uuid(userId);
        verify(companyMapper).toDTOList(companies);
    }

    @Test
    void shouldReturnEmptyListWhenUserDontHaveCompanies() {

        UUID userId = UUID.randomUUID();
        context(userId);

        List<Company> companies = List.of();
        List<CompanyDTO> expectedDTOs = List.of();

        when(companyRepository.findAllByOwner_Uuid(userId)).thenReturn(companies);
        when(companyMapper.toDTOList(companies)).thenReturn(expectedDTOs);

        ListResponse<CompanyDTO> result = companyService
                .getCompaniesByCurrentUser();

        assertEquals(expectedDTOs, result.getData());
        assertEquals(0, result.getTotal());

        verify(companyRepository).findAllByOwner_Uuid(userId);
        verify(companyMapper).toDTOList(companies);
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
