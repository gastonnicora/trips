package com.gastonnicora.trips.integration.company;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
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

    private String email;
    private UserDTO user;
    private final String password = "goodPassword";

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(new AddressResponse("calle falsa 123",
                        new Address("calle falsa", "123", "barrio", "ciudad", "departamento", "estado", "pais")));
        this.user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.email = user.getEmail();
        this.token = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(token);
        MvcResult result = companyApi
                .createCompany("Good Name", "goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        this.company = objectMapper.readValue(responseJson, CompanyDTO.class);
    }

    @Test
    void shouldDeleteCompanySuccessfully() throws Exception {
        companyApi.deleteCompany(company.getUuid()).andExpect(status().isOk());
        companyApi.getCompany(company.getUuid()).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

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
        UserDTO user2 = UserTestFactory.registerUser(mockMvc, "User2", password);
        String token2 = UserTestFactory.login(mockMvc, user2.getEmail(), password).getToken();
        companyApi.withToken(token2);
        companyApi.deleteCompany(company.getUuid())
                .andExpect(status().isForbidden());
    }
}
