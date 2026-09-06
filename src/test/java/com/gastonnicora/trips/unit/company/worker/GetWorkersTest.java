package com.gastonnicora.trips.unit.company.worker;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;

@ExtendWith(MockitoExtension.class)
class GetWorkersTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private UserService userService;

    @Mock
    private WorkerService workerService;

    @Test
    void shouldGetWorkerSuccessfully() {

        UUID companyUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "test@mail.com",
                "123"
        );
        company.setUuid(companyUuid);

        User user = new User(
                "John",
                "Doe",
                "john.doe@mail.com",
                "password",
                Set.of(Role.USER)
        );
        user.setUuid(userUuid);

        CompanyDTO companyDTO = new CompanyDTO();
        WorkerDTO expectedWorker = new WorkerDTO();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(companyMapper.toDTO(company))
                .thenReturn(companyDTO);

        when(userService.getUser(userUuid))
                .thenReturn(user);

        when(workerService.getWorkerByUserAndCompany(
                userUuid,
                companyUuid
        )).thenReturn(expectedWorker);

        WorkerDTO result = companyService.getWorker(
                userUuid,
                companyUuid
        );

        assertNotNull(result);
        assertEquals(expectedWorker, result);

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(companyMapper)
                .toDTO(company);

        verify(userService)
                .getUser(userUuid);

        verify(workerService)
                .getWorkerByUserAndCompany(userUuid, companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenCompanyDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> companyService.getWorker(userUuid, companyUuid)
        );

        verify(companyRepository)
                .findByUuid(companyUuid);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        Company company = new Company(
                "Test Company",
                "Buenos Aires, Argentina",
                -34.6037,
                -58.3816,
                "test@mail.com",
                "123"
        );
        company.setUuid(companyUuid);

        when(companyRepository.findByUuid(companyUuid))
                .thenReturn(Optional.of(company));

        when(companyMapper.toDTO(company))
                .thenReturn(new CompanyDTO());

        when(userService.getUser(userUuid))
                .thenThrow(new NotFoundException("User not found"));

        assertThrows(
                NotFoundException.class,
                () -> companyService.getWorker(userUuid, companyUuid)
        );

        verify(companyRepository)
                .findByUuid(companyUuid);

        verify(companyMapper)
                .toDTO(company);

        verify(userService)
                .getUser(userUuid);
    }
}
