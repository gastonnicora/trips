package com.gastonnicora.trips.unit.service.company;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.security.UserDetailsImpl;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.GeocodingService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
public class CreateCompanyTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private WorkerService workerService;

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
        context(userId);

        when(workerService.createWorker(user, company, Set.of(RoleCompany.OWNER))).thenReturn(new WorkerDTO());

        when(userService.getUser(userId)).thenReturn(user);
        when(geocodingService.obtenerDireccion(-34.6037, -58.3816))
                .thenReturn(addressResponse);

        when(companyRepository.save(any())).thenReturn(company);
        when(companyMapper.toDTO(company)).thenReturn(expectedDTO);

        CompanyDTO result = companyService.createCompany(request);

        assertEquals(expectedDTO, result);

        verify(geocodingService).obtenerDireccion(-34.6037, -58.3816);
        verify(companyRepository).save(any(Company.class));
        verify(companyMapper).toDTO(company);
        verify(workerService).createWorker(user, company, Set.of(RoleCompany.OWNER));
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

    private void context(UUID uuid) {
        // 🔧 Mock SecurityContext
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUuid()).thenReturn(uuid);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }
}
