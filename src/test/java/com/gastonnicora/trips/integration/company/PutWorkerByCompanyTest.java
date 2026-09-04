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
class PutWorkerByCompanyTest {

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

    private UserDTO createWorkerWithRole(RoleCompany role) throws Exception {
        UserDTO newWorker = UserTestFactory.registerUser(
                mockMvc,
                "worker_" + role.name(),
                "goodPassword"
        );

        companyApi
                .createWorker(
                        companyUuid,
                        newWorker.getUuid(),
                        Set.of(role)
                )
                .andExpect(status().isOk());

        return newWorker;
    }

    private CompanyApiTestClient loginAs(UserDTO user) throws Exception {
        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                "goodPassword"
        );

        return new CompanyApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());
    }

    @Test
    void shouldUpdateWorkerRolesSuccessfullyAsOwner() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        companyApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }

    @Test
    void shouldUpdateWorkerRolesSuccessfullyAsAdmin() throws Exception {
        UserDTO admin = createWorkerWithRole(RoleCompany.ADMIN);

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        CompanyApiTestClient adminApi = loginAs(admin);

        adminApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("SELLER"));
    }

    @Test
    void shouldUpdateWorkerRolesSuccessfullyAsHrManager() throws Exception {
        UserDTO hrManager = createWorkerWithRole(RoleCompany.HR_MANAGER);

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        CompanyApiTestClient hrManagerApi = loginAs(hrManager);

        hrManagerApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("SELLER"));
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToUpdateWorker() throws Exception {
        UserDTO driver = createWorkerWithRole(RoleCompany.DRIVER);

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER)
                )
                .andExpect(status().isOk());

        CompanyApiTestClient driverApi = loginAs(driver);

        driverApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenSellerTriesToUpdateWorker() throws Exception {
        UserDTO seller = createWorkerWithRole(RoleCompany.SELLER);

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        CompanyApiTestClient sellerApi = loginAs(seller);

        sellerApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateWorker() throws Exception {
        UserDTO normalUser = UserTestFactory.registerUser(
                mockMvc,
                "normal_user",
                "goodPassword"
        );

        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        CompanyApiTestClient userApi = loginAs(normalUser);

        userApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
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
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenRolesAreEmpty() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        companyApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of()
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAssigningOwnerRole() throws Exception {
        companyApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        companyApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.OWNER)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenWorkerDoesNotExist() throws Exception {
        companyApi
                .updateWorkerRoles(
                        companyUuid,
                        UUID.randomUUID(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        companyApi
                .updateWorkerRoles(
                        UUID.randomUUID(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isForbidden());
    }
}
