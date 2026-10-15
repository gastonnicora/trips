package com.gastonnicora.trips.unit.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.Company;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToListResponseTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private GeocodingService geocodingService;

    @Test
    void shouldConvertCompaniesToListResponse() {
        List<Company> companies = List.of(new Company(), new Company());
        List<CompanyDTO> expectedDTOs = List.of(new CompanyDTO(), new CompanyDTO());

        when(companyMapper.toDTOList(companies)).thenReturn(expectedDTOs);

        ListResponse<CompanyDTO> result = companyService.toListResponse(companies);

        assertEquals(expectedDTOs, result.getData());
        assertEquals(2, result.getTotal());
        verify(companyMapper).toDTOList(companies);
    }
}
