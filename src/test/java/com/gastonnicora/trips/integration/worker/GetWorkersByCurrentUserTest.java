package com.gastonnicora.trips.integration.worker;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.WorkerApiTestClient;
import com.gastonnicora.trips.helpers.WorkerTestFactory;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetWorkersByCurrentUserTest {

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
    private final String password = "goodPassword";

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        String ownerToken = registerAndLoginUser("ownerComapany");
        this.company = new CompanyTestFactory(mockMvc, objectMapper, ownerToken, geocodingService).createCompany();
        registerAndLoginUser("worker");
        new WorkerTestFactory(mockMvc, objectMapper, ownerToken).createWorker(company.getUuid(), user.getUuid(), Set.of(RoleCompany.DRIVER));
        
        workerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    private String registerAndLoginUser(String nombre) throws Exception {
        this.user = UserTestFactory.registerUser(mockMvc, nombre, this.password);
        this.email = user.getEmail();
        this.token = UserTestFactory.login(mockMvc, email, this.password).getToken();
        return this.token;
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
    void shouldReturnNotFoundWhenUserHasNoWorkers() throws Exception {
        registerAndLoginUser("newUser");
        workerApi.withToken(token).getWorkersByCurrentUser()
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        workerApi.withToken(null);
        workerApi.getWorkersByCurrentUser()
                .andExpect(status().isUnauthorized());
    }
}
