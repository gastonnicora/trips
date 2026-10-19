package com.gastonnicora.trips.integration.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.services.GeocodingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetCompaniesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    @Value("${superadmin.email}")
    private String email;
    @Value("${superadmin.password}")
    private String password;

    private CompanyApiTestClient companyApi;
    private String tokenUser;
    private String tokenAdmin;
    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.tokenUser = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
        this.company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                tokenUser,
                geocodingService
        ).createCompany();
        this.tokenAdmin = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(tokenAdmin);
    }

    @Test
    void shouldReturnOkWhenCompaniesExist() throws Exception {
        companyApi.getCompanies()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].uuid", hasItem(company.getUuid().toString())))
                .andExpect(jsonPath("$[*].name", hasItem(company.getName())))
                .andExpect(jsonPath("$[*].email", hasItem(company.getEmail())));
    }

    @Test
    void shouldReturnAllCompaniesWhenThereAreTwo() throws Exception {
        CompanyDTO company2 = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                tokenUser,
                geocodingService
        ).createCompany();

        companyApi.getCompanies()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItems(company.getName(), company2.getName())));
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        companyApi.withToken(tokenUser);
        companyApi.getCompanies()
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompanies()
                .andExpect(status().isUnauthorized());
    }
}