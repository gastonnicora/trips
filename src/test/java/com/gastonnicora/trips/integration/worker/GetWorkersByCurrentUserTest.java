package com.gastonnicora.trips.integration.worker;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.worker.WorkerApiTestClient;
import com.gastonnicora.trips.helpers.worker.WorkerTestFactory;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetWorkersByCurrentUserTest {

    private final String password = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private GeocodingService geocodingService;
    private String token;
    private WorkerApiTestClient workerApi;
    private String email;
    private UserDTO user;
    private CompanyDTO company;

    private String registerAndLoginUser(String nombre) throws Exception {
        this.user = UserTestFactory.registerUser(mockMvc, nombre, this.password);
        this.email = user.getEmail();
        this.token = UserTestFactory.login(mockMvc, email, this.password).getToken();
        return this.token;
    }

    @BeforeEach
    void setup() throws Exception {
        String ownerToken = registerAndLoginUser("ownerComapany");
        this.company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService).createCompany();
        registerAndLoginUser("worker");
        new WorkerTestFactory(mockMvc, objectMapper, ownerToken).createWorker(company.getUuid(), user.getUuid(), Set.of(RoleCompany.DRIVER));

        workerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    @Test
    void shouldReturnNotFoundWhenUserHasNoWorkers() throws Exception {
        registerAndLoginUser("newUser");
        workerApi.withToken(token).getWorkersByCurrentUser()
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnOkWhenUserHasWorkers() throws Exception {
        workerApi.getWorkersByCurrentUser()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(user.getName()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andExpect(jsonPath("$.workers").isArray())
                .andExpect(jsonPath("$.workers").isNotEmpty())
                .andExpect(jsonPath("$.workers[0].roles").isArray())
                .andExpect(jsonPath("$.workers[0].roles[0]").value("DRIVER"));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        workerApi.withToken(null);
        workerApi.getWorkersByCurrentUser()
                .andExpect(status().isUnauthorized());
    }
}
