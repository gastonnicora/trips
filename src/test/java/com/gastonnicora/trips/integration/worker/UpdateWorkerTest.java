package com.gastonnicora.trips.integration.worker;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UpdateWorkerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    private WorkerApiTestClient workerApi;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO owner;
    private UserDTO worker;

    private UUID companyUuid;

    private UserDTO createWorkerWithRole(RoleCompany role) throws Exception {
        UserDTO newWorker = UserTestFactory.registerUser(
                mockMvc,
                "worker_" + role.name(),
                "goodPassword"
        );

        workerApi
                .createWorker(
                        companyUuid,
                        newWorker.getUuid(),
                        Set.of(role)
                )
                .andExpect(status().isOk());

        return newWorker;
    }

    private WorkerApiTestClient loginAs(UserDTO user) throws Exception {
        LoginResponse login = UserTestFactory.login(
                mockMvc,
                user.getEmail(),
                "goodPassword"
        );

        return new WorkerApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());
    }

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

        workerApi = new WorkerApiTestClient(
                mockMvc,
                new ObjectMapper()
        ).withToken(login.getToken());

        companyUuid = new CompanyTestFactory(mockMvc, objectMapper, login.getToken(), geocodingService).createCompany().getUuid();

        worker = UserTestFactory.registerUser(
                mockMvc,
                "worker",
                "goodPassword"
        );
    }

    @Test
    void shouldReturnBadRequestWhenAssigningOwnerRole() throws Exception {
        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        workerApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.OWNER)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRolesAreEmpty() throws Exception {
        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        workerApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of()
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        workerApi
                .updateWorkerRoles(
                        UUID.randomUUID(),
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenDriverTriesToUpdateWorker() throws Exception {
        UserDTO driver = createWorkerWithRole(RoleCompany.DRIVER);

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.SELLER)
                )
                .andExpect(status().isOk());

        WorkerApiTestClient driverApi = loginAs(driver);

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

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        WorkerApiTestClient sellerApi = loginAs(seller);

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

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        WorkerApiTestClient userApi = loginAs(normalUser);

        userApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenWorkerDoesNotExist() throws Exception {
        workerApi
                .updateWorkerRoles(
                        companyUuid,
                        UUID.randomUUID(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        WorkerApiTestClient unauthorizedApi = new WorkerApiTestClient(
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
    void shouldUpdateWorkerRolesSuccessfullyAsAdmin() throws Exception {
        UserDTO admin = createWorkerWithRole(RoleCompany.ADMIN);

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        WorkerApiTestClient adminApi = loginAs(admin);

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

        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        WorkerApiTestClient hrManagerApi = loginAs(hrManager);

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
    void shouldUpdateWorkerRolesSuccessfullyAsOwner() throws Exception {
        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());

        workerApi
                .updateWorkerRoles(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.ADMIN)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"));
    }
}
