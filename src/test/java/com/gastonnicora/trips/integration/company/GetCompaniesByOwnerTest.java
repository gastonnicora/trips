package com.gastonnicora.trips.integration.company;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetCompaniesByOwnerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

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
        this.company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                token,
                geocodingService
        ).createCompany();
        this.tokenAdmin = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi.withToken(tokenAdmin);
    }

    @Test
    void shouldReturnOkWhenUserHasCompanies() throws Exception {
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
    void shouldReturnOkWhenUserHasTwoCompanies() throws Exception {
        CompanyDTO company2 = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                token,
                geocodingService
        ).createCompany();
        companyApi.withToken(tokenAdmin);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name", hasItems(company.getName(), company2.getName())))
                .andExpect(jsonPath("$.data[*].email", hasItems(company.getEmail(), company2.getEmail())))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        companyApi.withToken(token);
        companyApi.getCompaniesByOwner(user.getUuid())
                .andExpect(status().isForbidden());
    }
}
