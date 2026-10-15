package com.gastonnicora.trips.integration.company;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
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
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetCompaniesByCurrentUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private String token;
    private CompanyApiTestClient companyApi;

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        this.token = UserTestFactory.registerAndLogin(mockMvc);
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);
        this.company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                token,
                geocodingService
        ).createCompany();
    }

    @Test
    void shouldReturnOkWhenUserHasCompanies() throws Exception {
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(company.getName()))
                .andExpect(jsonPath("$.data[0].email").value(company.getEmail()))
                .andExpect(jsonPath("$.data[0].phone").value(company.getPhone()))
                .andExpect(jsonPath("$.data[0].latitude").value(company.getLatitude()))
                .andExpect(jsonPath("$.data[0].longitude").value(company.getLongitude()))
                .andExpect(jsonPath("$.data[0].address").value(company.getAddress()));
    }

    @Test
    void shouldReturnOkWhenUserHasNoCompanies() throws Exception {
        this.token = UserTestFactory.registerAndLogin(mockMvc);
        this.companyApi = this.companyApi.withToken(token);
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Empresas no encontradas"));
    }

    @Test
    void shouldReturnOkWhenUserHasTwoCompanies() throws Exception {
        MvcResult result = companyApi
                .createCompany("Good Name2", "goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        CompanyDTO company2 = objectMapper.readValue(responseJson, CompanyDTO.class);
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].name", hasItems(company.getName(), company2.getName())))
                .andExpect(jsonPath("$.data[*].email", hasItems(company.getEmail(), company2.getEmail())))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isUnauthorized());
    }

}
