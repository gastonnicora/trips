package com.gastonnicora.trips.unit.company;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.services.GeocodingService;

@ExtendWith(MockitoExtension.class)
class GetCompanyEntityTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private GeocodingService geocodingService;

    @Test
    void shouldReturnCompanyEntitySuccessfully() {
        Company company = new Company();
        UUID uuid = UUID.randomUUID();
        company.setUuid(uuid);

        when(companyRepository.findByUuidAndActiveTrue(uuid))
                .thenReturn(Optional.of(company));

        Company result = companyService.getCompanyEntity(uuid);

        assertEquals(company, result);
        verify(companyRepository).findByUuidAndActiveTrue(uuid);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(companyRepository.findByUuidAndActiveTrue(uuid))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> companyService.getCompanyEntity(uuid));

        assertEquals(404, exception.getStatus());
        assertEquals("Empresa no encontrada", exception.getMessage());
        verify(companyRepository).findByUuidAndActiveTrue(uuid);
    }
}
