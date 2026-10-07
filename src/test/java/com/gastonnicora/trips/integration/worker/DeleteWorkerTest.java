package com.gastonnicora.trips.integration.worker;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.enums.RoleCompany;
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
class DeleteWorkerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GeocodingService geocodingService;

    private WorkerApiTestClient workerApi;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO owner;
    private UserDTO worker, otherWorker;

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
        companyUuid = new CompanyTestFactory(mockMvc, objectMapper, login.getToken(), geocodingService).createCompany().getUuid();

        worker = UserTestFactory.registerUser(
                mockMvc,
                "worker",
                "goodPassword"
        );
        workerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(login.getToken());
        workerApi
                .createWorker(
                        companyUuid,
                        worker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());
        otherWorker = UserTestFactory.registerUser(
                mockMvc,
                "worker",
                "goodPassword"
        );
        workerApi
                .createWorker(
                        companyUuid,
                        otherWorker.getUuid(),
                        Set.of(RoleCompany.DRIVER)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteWorkerSuccessfully() throws Exception {

        workerApi
                .deleteWorker(
                        companyUuid,
                        worker.getUuid()
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenAdminDeletesWorker() throws Exception {
        workerApi.updateWorkerRoles(companyUuid, worker.getUuid(), Set.of(RoleCompany.ADMIN)).andExpect(status().isOk());
        LoginResponse adminLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        workerApi.withToken(adminLogin.getToken());

        workerApi
                .deleteWorker(
                        companyUuid,
                        otherWorker.getUuid()
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenHRManagerDeletesWorker() throws Exception {

        workerApi.updateWorkerRoles(companyUuid, worker.getUuid(), Set.of(RoleCompany.HR_MANAGER)).andExpect(status().isOk());
        LoginResponse HRLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        workerApi.withToken(HRLogin.getToken());

        workerApi.deleteWorker(
                companyUuid,
                otherWorker.getUuid()
        )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenWorkerDoesNotExist() throws Exception {
        workerApi
                .deleteWorker(
                        companyUuid,
                        UUID.randomUUID()
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {
        workerApi
                .deleteWorker(
                        UUID.randomUUID(),
                        worker.getUuid()
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        WorkerApiTestClient unauthorizedApi = new WorkerApiTestClient(
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

        LoginResponse driverLogin = UserTestFactory.login(
                mockMvc,
                worker.getEmail(),
                "goodPassword"
        );

        workerApi.withToken(driverLogin.getToken())
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
        workerApi.withToken(userLogin.getToken())
                .deleteWorker(
                        companyUuid,
                        owner.getUuid()
                )
                .andExpect(status().isForbidden());
    }

}
