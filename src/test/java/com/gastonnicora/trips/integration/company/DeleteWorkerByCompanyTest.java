package com.gastonnicora.trips.integration.company;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteWorkerByCompanyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyApiTestClient companyApi;

    private UserDTO owner;
    private UserDTO worker;

    private UUID companyUuid;

    @BeforeEach
    void setup() throws Exception {
        owner = UserTestFactory.registerUser(
                mockMvc,
                "owner",
                "goodPassword"
        );

        LoginResponse login = UserTestFactory.login(
                mockMvc,
                owner.getEmail(),
                "goodPassword"
        );

        companyApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());

        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(new AddressResponse(
                        "calle falsa 123",
                        new Address(
                                "calle falsa",
                                "123",
                                "barrio",
                                "ciudad",
                                "departamento",
                                "estado",
                                "pais"
                        )
                ));

        companyUuid = createCompany();

        worker = UserTestFactory.registerUser(
                mockMvc,
                "worker",
                "goodPassword"
        );
    }

    private UUID createCompany() throws Exception {
        String response = companyApi
                .createCompany(
                        "Test Company",
                        "company@test.com",
                        "123456789",
                        -34.6037,
                        -58.3816
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CompanyDTO company = new ObjectMapper()
                .readValue(response, CompanyDTO.class);

        return company.getUuid();
    }

    @Test
    void shouldDeleteWorkerSuccessfully() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        companyApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isOk());
    }

    
    @Test
    void shouldReturnOkWhenAdminDeletesWorker() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isOk());

        LoginResponse adminLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        CompanyApiTestClient adminApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(adminLogin.getToken());

        adminApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isOk());
    }

     
    @Test
    void shouldReturnOkWhenHR_ManagerDeletesWorker() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.HR_MANAGER)
                )
                .andExpect(status().isOk());

        LoginResponse adminLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        CompanyApiTestClient adminApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(adminLogin.getToken());

        adminApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenWorkerDoesNotExist() throws Exception {
        companyApi
                .deleteWorker(
                        companyUuid,
                        UUID.randomUUID()
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        companyApi
                .deleteWorker(
                        UUID.randomUUID(),
                        worker.getUuid()
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        CompanyApiTestClient unauthorizedApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        );

        unauthorizedApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToDeleteWorker() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        LoginResponse driverLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        CompanyApiTestClient driverApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(driverLogin.getToken());

        driverApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteWorker() throws Exception {

        LoginResponse userLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        CompanyApiTestClient userApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(userLogin.getToken());

        userApi
                .deleteWorker(
                        companyUuid,
                        owner.getUuid()
                )
                .andExpect(status().isForbidden());
    }

}
