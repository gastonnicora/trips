package com.gastonnicora.trips.integration.worker;

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
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateWorkerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO owner;
    private UserDTO worker;
    private CompanyDTO company;

    private WorkerApiTestClient workerApi;

    @BeforeEach
    void setup() throws Exception {

        owner = UserTestFactory.registerUser(
                mockMvc,
                "owner",
                "goodPassword");

        worker = UserTestFactory.registerUser(
                mockMvc,
                "worker",
                "goodPassword");

        LoginResponse login = UserTestFactory.login(
                mockMvc,
                owner.getEmail(),
                "goodPassword");

        workerApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(login.getToken());


        company = new CompanyTestFactory(mockMvc, objectMapper, login.getToken(), geocodingService).createCompany();
    }

    @Test
    void shouldCreateWorkerSuccessfully() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.user.uuid")
                        .value(worker.getUuid().toString()))
                .andExpect(jsonPath("$.company.uuid")
                        .value(company.getUuid().toString()))
                .andExpect(jsonPath("$.roles")
                        .isArray())
                .andExpect(jsonPath("$.roles[0]")
                        .value("DRIVER"))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldCreateWorkerWithMultipleRoles() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(
                                RoleCompany.DRIVER,
                                RoleCompany.SELLER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles.length()").value(2));
    }

    @Test
    void shouldReturnConflictWhenUserIsAlreadyWorker() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenOwnerRoleIsAssigned() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.OWNER))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRolesAreEmpty() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        UUID.randomUUID(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {

        workerApi
                .createWorker(
                        UUID.randomUUID(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowOwnerToCreateWorker() throws Exception {

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "ownerCreate",
                "goodPassword");

        workerApi
                .createWorker(
                        company.getUuid(),
                        anotherUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToCreateWorker() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN))
                .andExpect(status().isOk());

        LoginResponse adminLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword");

        WorkerApiTestClient adminApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(adminLogin.getToken());

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "adminCreate",
                "goodPassword");

        adminApi
                .createWorker(
                        company.getUuid(),
                        anotherUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowHrManagerToCreateWorker() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.HR_MANAGER))
                .andExpect(status().isOk());

        LoginResponse hrLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword");

        WorkerApiTestClient hrApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(hrLogin.getToken());

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "hrCreate",
                "goodPassword");

        hrApi
                .createWorker(
                        company.getUuid(),
                        anotherUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenSellerTriesToCreateWorker() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER))
                .andExpect(status().isOk());

        LoginResponse sellerLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword");

        WorkerApiTestClient sellerApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(sellerLogin.getToken());

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "sellerCreate",
                "goodPassword");

        sellerApi
                .createWorker(
                        company.getUuid(),
                        anotherUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToCreateWorker() throws Exception {

        workerApi
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk());

        LoginResponse driverLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword");

        WorkerApiTestClient driverApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(driverLogin.getToken());

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "driverCreate",
                "goodPassword");

        driverApi
                .createWorker(
                        company.getUuid(),
                        anotherUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotHaveCompanyRole() throws Exception {

        UserDTO anotherUser = UserTestFactory.registerUser(
                mockMvc,
                "another",
                "goodPassword");

        LoginResponse userLogin = UserTestFactory.login(
                mockMvc,
                anotherUser.getEmail(),
                "goodPassword");

        WorkerApiTestClient userApi = new WorkerApiTestClient(
                mockMvc,
                objectMapper)
                .withToken(userLogin.getToken());

        UserDTO targetUser = UserTestFactory.registerUser(
                mockMvc,
                "target",
                "goodPassword");

        userApi
                .createWorker(
                        company.getUuid(),
                        targetUser.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        WorkerApiTestClient apiWithoutToken = new WorkerApiTestClient(
                mockMvc,
                objectMapper);

        apiWithoutToken
                .createWorker(
                        company.getUuid(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER))
                .andExpect(status().isUnauthorized());
    }
}
