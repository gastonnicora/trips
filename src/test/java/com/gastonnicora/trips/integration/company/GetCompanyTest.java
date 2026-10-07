package com.gastonnicora.trips.integration.company;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
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
class GetCompanyTest {

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

        token = UserTestFactory.registerAndLogin(mockMvc);
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);

        this.company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();
    }

    @Test
    void shouldReturnOkWhenCompanyExists() throws Exception {
        companyApi.getCompany(company.getUuid())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(company.getName()))
                .andExpect(jsonPath("$.email").value(company.getEmail()))
                .andExpect(jsonPath("$.phone").value(company.getPhone()))
                .andExpect(jsonPath("$.latitude").value(company.getLatitude()))
                .andExpect(jsonPath("$.longitude").value(company.getLongitude()))
                .andExpect(jsonPath("$.address").value(company.getAddress()));
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompany(company.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        companyApi.getCompany(nonExistentUuid)
                .andExpect(status().isNotFound());
    }
}
