package com.gastonnicora.trips.unit.service.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class GetCompaniesByOwnerTest {
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Test
    void shouldGetCompaniesByOwnerSuccessfully() {

        User user = new User();

        Company company = new Company("Test", user, "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company.setUuid(UUID.randomUUID());

        Company company2 = new Company("Test2", user, "Buenos Aires, Argentina", -34.6037, -58.3846,
                "test@mail.com", "123");
        company2.setUuid(UUID.randomUUID());

        List<Company> companies = List.of(company, company2);

        when(companyRepository.findAllByOwner_Uuid(any())).thenReturn(companies);

        List<CompanyDTO> expectedDTOs = List.of(new CompanyDTO(), new CompanyDTO());

        when(companyMapper.toDTOList(companies)).thenReturn(expectedDTOs);

        ListResponse<CompanyDTO> result = companyService.getCompaniesByUser(UUID.randomUUID());

        assertEquals(expectedDTOs, result.getData());
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotal());

        verify(companyRepository).findAllByOwner_Uuid(any());
        verify(companyMapper).toDTOList(companies);

    }

    @Test
    void shouldReturnEmptyListWhenUserDontHaveCompanies() {

        List<Company> companies = List.of();

        when(companyRepository.findAllByOwner_Uuid(any())).thenReturn(companies);

        List<CompanyDTO> expectedDTOs = List.of();

        when(companyMapper.toDTOList(companies)).thenReturn(expectedDTOs);

        ListResponse<CompanyDTO> result = companyService.getCompaniesByUser(UUID.randomUUID());

        assertEquals(expectedDTOs, result.getData());
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getTotal());

        verify(companyRepository).findAllByOwner_Uuid(any());
        verify(companyMapper).toDTOList(companies);

    }
}
