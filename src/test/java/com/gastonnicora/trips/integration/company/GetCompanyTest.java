package com.gastonnicora.trips.integration.company;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetCompanyTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private CompanyApiTestClient companyApi;

    private String email;
    private final String password = "goodPassword";

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.email = user.getEmail();
        token = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);
        MvcResult result = companyApi.createCompany("Good Name","goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        this.company = objectMapper.readValue(responseJson, CompanyDTO.class);
    }

    @Test
    void shouldReturnOk_whenCompanyExists() throws Exception {
        companyApi.getCompany(company.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(company.getName()))
                .andExpect(jsonPath("$.email").value(company.getEmail()))
                .andExpect(jsonPath("$.phone").value(company.getPhone()))
                .andExpect(jsonPath("$.latitude").value(company.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(company.getLongitude()))
                .andExpect(jsonPath("$.owner.email").value(email))
                .andExpect(jsonPath("$.address").value(company.getAddress()));
    }


    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompany(company.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        companyApi.getCompany(nonExistentUuid)
                .andExpect(status().isNotFound());
    }
}
