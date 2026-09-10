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
public class DeleteCompanyTest {

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

        token = UserTestFactory
                .registerAndLogin(mockMvc);

        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);

        this.company = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                token,
                geocodingService
        ).createCompany();

    }

    @Test
    void shouldDeleteCompanySuccessfully() throws Exception {
        companyApi.deleteCompany(company.getUuid()).andExpect(status().isOk());
        companyApi.getCompany(company.getUuid()).andExpect(status().isNotFound());

    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.deleteCompany(company.getUuid())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnForbidden_whenCompanyDoesNotExist() throws Exception {
        companyApi.deleteCompany(UUID.randomUUID())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbidden_whenUserIsNotOwner() throws Exception {
        String token2 = UserTestFactory
                .registerAndLogin(mockMvc);
        companyApi.withToken(token2);
        companyApi.deleteCompany(company.getUuid())
                .andExpect(status().isForbidden());
    }
}
