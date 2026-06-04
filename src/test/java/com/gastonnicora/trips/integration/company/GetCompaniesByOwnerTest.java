package com.gastonnicora.trips.integration.company;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasItems;

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
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetCompaniesByOwnerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenAdmin;
    private CompanyApiTestClient companyApi;
    @Value("${superadmin.email}")
    private String email;
    @Value("${superadmin.password}")
    private String password;
    private UserDTO user;
    private String token;

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        this.user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.token = UserTestFactory.login(mockMvc, this.user.getEmail(), password).getToken();
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);
        MvcResult result = companyApi
                .createCompany("Good Name", "goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        this.company = objectMapper.readValue(responseJson, CompanyDTO.class);
        this.tokenAdmin = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi.withToken(tokenAdmin);
    }

    @Test
    void shouldReturnOk_whenUserHaveAnyCompany() throws Exception {
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(company.getName()))
                .andExpect(jsonPath("$.data[0].email").value(company.getEmail()))
                .andExpect(jsonPath("$.data[0].phone").value(company.getPhone()))
                .andExpect(jsonPath("$.data[0].latitude").value(company.getLatitude()))
                .andExpect(jsonPath("$.data[0].longitude").value(company.getLongitude()))
                .andExpect(jsonPath("$.data[0].address").value(company.getAddress()));
    }

    @Test
    void shouldReturnOk_whenUserHaveTwoCompany() throws Exception {
        companyApi.withToken(token);
        MvcResult result = companyApi
                .createCompany("Good Name2", "goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        CompanyDTO company2 = objectMapper.readValue(responseJson, CompanyDTO.class);
        companyApi.withToken(tokenAdmin);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name", hasItems(company.getName(), company2.getName())))
                .andExpect(jsonPath("$.data[*].email", hasItems(company.getEmail(), company2.getEmail())))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnListEmpty_whenUserDoesNotExist() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        companyApi.getCompaniesByOwner(nonExistentUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void shouldReturnForbidden_whenUserIsNotIsAdmin() throws Exception {
        companyApi.withToken(token);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isForbidden());
    }
}
