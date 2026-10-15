package com.gastonnicora.trips.unit.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateCompanySuccessfully() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUuid(userId);

        CompanyCreate request = new CompanyCreate();
        request.setName("Test");
        request.setEmail("test@mail.com");
        request.setPhone("123");
        request.setLatitude(-34.6037);
        request.setLongitude(-58.3816);

        AddressResponse addressResponse = new AddressResponse("Buenos Aires, Argentina", new Address(
                "calle 2", "123", "barrio", "ciudad", "departamento", "estado", "pais"));

        Company company = new Company();

        CompanyDTO expectedDTO = new CompanyDTO();

        when(geocodingService.obtenerDireccion(-34.6037, -58.3816))
                .thenReturn(addressResponse);

        when(companyRepository.save(any())).thenReturn(company);
        when(companyMapper.toDTO(company)).thenReturn(expectedDTO);

        CompanyDTO result = companyService.createCompany(request);

        assertEquals(expectedDTO, result);

        verify(geocodingService).obtenerDireccion(-34.6037, -58.3816);
        verify(companyRepository).save(any(Company.class));
        verify(companyMapper).toDTO(company);
    }

    @Test
    void shouldThrowBadRequestWhenAddressIsNull() {

        CompanyCreate request = new CompanyCreate();
        request.setLatitude(-34.6037);
        request.setLongitude(-58.3816);

        AddressResponse response = new AddressResponse(null, null);

        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(response);

        assertThrows(BadRequestException.class, () -> companyService.createCompany(request));
    }

}
