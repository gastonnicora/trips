package com.gastonnicora.trips.integration.company;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetWorkersByCompanyTest {

    @Autowired
    private MockMvc mockMvc;

    private CompanyApiTestClient companyApi;

    private UserDTO user;
    private String token;
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

        token = login.getToken();

        companyApi = new CompanyApiTestClient(
                mockMvc,
                new tools.jackson.databind.ObjectMapper()
        ).withToken(token);

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

        ObjectMapper mapper = new ObjectMapper();

        CompanyDTO company = mapper.readValue(response, CompanyDTO.class);

        return company.getUuid();
    }

    @Test
    void shouldGetWorkersByCompanySuccessfully() throws Exception {
        companyApi
                .getWorkersByCompany(companyUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workers").exists())
                .andExpect(jsonPath("$.workers").isArray());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        CompanyApiTestClient unauthorizedApi
                = new CompanyApiTestClient(
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
