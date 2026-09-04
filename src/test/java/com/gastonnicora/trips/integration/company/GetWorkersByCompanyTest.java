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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class GetWorkersByCompanyTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyApiTestClient companyApi;

    private UserDTO user;
    private UUID companyUuid;

    private final String name = "user";
    private final String pass = "goodPassword";

    @BeforeEach
    void setup() throws Exception {
        user = UserTestFactory.registerUser(mockMvc, name, pass);

        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                pass
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

    private CompanyApiTestClient loginAs(UserDTO user) throws Exception {
        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                pass
        );

        return new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());
    }

    private UserDTO createWorkerWithRole(RoleCompany role) throws Exception {
        UserDTO worker = UserTestFactory.registerUser(
                mockMvc,
                "worker_" + role.name(),
                pass
        );

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(role)
                )
                .andExpect(status().isOk());

        return worker;
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsOwner() throws Exception {
        companyApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsAdmin() throws Exception {
        UserDTO admin = createWorkerWithRole(RoleCompany.ADMIN);

        CompanyApiTestClient adminApi = loginAs(admin);

        adminApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldGetWorkersByCompanySuccessfullyAsHrManager() throws Exception {
        UserDTO hrManager = createWorkerWithRole(RoleCompany.HR_MANAGER);

        CompanyApiTestClient hrManagerApi = loginAs(hrManager);

        hrManagerApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToGetWorkers() throws Exception {
        UserDTO driver = createWorkerWithRole(RoleCompany.DRIVER);

        CompanyApiTestClient driverApi = loginAs(driver);

        driverApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenSellerTriesToGetWorkers() throws Exception {
        UserDTO seller = createWorkerWithRole(RoleCompany.SELLER);

        CompanyApiTestClient sellerApi = loginAs(seller);

        sellerApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToGetWorkers() throws Exception {
        UserDTO normalUser = UserTestFactory.registerUser(
                mockMvc,
                "normal_user",
                pass
        );

        CompanyApiTestClient userApi = loginAs(normalUser);

        userApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        CompanyApiTestClient unauthorizedApi = new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        );

        unauthorizedApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        companyApi
                .getWorkersByCompany(UUID.randomUUID())
                .andExpect(status().isForbidden());
    }
}
