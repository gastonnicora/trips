package com.gastonnicora.trips.unit.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCompaniesTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private GeocodingService geocodingService;

    @Test
    void shouldReturnCompaniesSuccessfully() {
        List<Company> companies = List.of(new Company(), new Company());
        List<CompanyDTO> dtos = List.of(new CompanyDTO(), new CompanyDTO());

        when(companyRepository.findAllByActiveTrue()).thenReturn(companies);
        when(companyMapper.toDTOList(companies)).thenReturn(dtos);

        List<CompanyDTO> result = companyService.getCompanies();

        assertEquals(dtos, result);
        verify(companyRepository).findAllByActiveTrue();
        verify(companyMapper).toDTOList(companies);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenThereAreNoCompanies() {
        when(companyRepository.findAllByActiveTrue()).thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> companyService.getCompanies()
                                                  );

        assertEquals(404, exception.getStatus());
        assertEquals("No hay compañias registradas", exception.getMessage());
        verify(companyRepository).findAllByActiveTrue();
        verify(companyMapper, never()).toDTOList(anyList());
    }
}