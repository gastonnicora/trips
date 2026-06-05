package com.gastonnicora.trips.unit.service.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import com.gastonnicora.trips.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UpdateCompanyTest {
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

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldUpdateCompanySuccessfully() {

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
        Company company2 = new Company("Test2", "Test2", -34.6037, -58.3816, "test2@mail.com", "123");
        company2.setUuid(UUID.randomUUID());

        CompanyDTO expectedDTO = new CompanyDTO();

        when(companyRepository.findByUuid(any())).thenReturn(java.util.Optional.of(company2));
        when(geocodingService.obtenerDireccion(-34.6037, -58.3816))
                .thenReturn(addressResponse);

        when(companyRepository.save(any())).thenReturn(company);
        when(companyMapper.toDTO(company)).thenReturn(expectedDTO);

        CompanyDTO result = companyService.updateCompany(company2.getUuid(), request);

        assertEquals(expectedDTO, result);

        verify(geocodingService).obtenerDireccion(-34.6037, -58.3816);
        verify(companyRepository).findByUuid(company2.getUuid());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCompanyNotFound() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUuid(userId);

        CompanyCreate request = new CompanyCreate();
        request.setName("Test");
        request.setEmail("test@mail.com");
        request.setPhone("123");
        request.setLatitude(-34.6037);
        request.setLongitude(-58.3816);

        when(companyRepository.findByUuid(any())).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> companyService.updateCompany(UUID.randomUUID(), request));
        verify(companyRepository).findByUuid(any());
    }

    @Test
    void shouldThrowBadRequestWhenAddressIsNull() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUuid(userId);

        Company company = new Company();
        company.setUuid(UUID.randomUUID());

        when(companyRepository.findByUuid(any())).thenReturn(java.util.Optional.of(company));

        CompanyCreate request = new CompanyCreate();
        request.setLatitude(-34.6037);
        request.setLongitude(-58.3816);

        AddressResponse response = new AddressResponse(null, null);

        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(response);
        assertThrows(BadRequestException.class, () -> companyService.updateCompany(company.getUuid(), request));
        verify(companyRepository).findByUuid(any());
        verify(geocodingService).obtenerDireccion(anyDouble(), anyDouble());

    }

}
