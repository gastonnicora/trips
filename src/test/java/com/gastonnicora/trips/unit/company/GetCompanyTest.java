package com.gastonnicora.trips.unit.company;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;

@ExtendWith(MockitoExtension.class)
public class GetCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Test
    void shouldGetCompanySuccessfully() {

        Company company = new Company("Test", "Buenos Aires, Argentina", -34.6037, -58.3816,
                "test@mail.com", "123");
        company.setUuid(UUID.randomUUID());

        when(companyRepository.findByUuid(company.getUuid())).thenReturn(java.util.Optional.of(company));

        CompanyDTO expectedDTO = new CompanyDTO();

        when(companyMapper.toDTO(company)).thenReturn(expectedDTO);

        CompanyDTO result = companyService.getCompany(company.getUuid());

        assertEquals(expectedDTO, result);

        verify(companyRepository).findByUuid(company.getUuid());
        verify(companyMapper).toDTO(company);

        assertEquals(expectedDTO, result);

    }

    @Test
    void shouldThrowBadRequestWhenCompanyNotFound() {

        when(companyRepository.findByUuid(any())).thenReturn(java.util.Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> companyService.getCompany(UUID.randomUUID()));

        assertEquals("Empresa no encontrada", ex.getMessage());

        verify(companyRepository).findByUuid(any());

    }
}
