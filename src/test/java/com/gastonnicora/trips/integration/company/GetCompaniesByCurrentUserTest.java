package com.gastonnicora.trips.integration.company;

import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class GetCompaniesByCurrentUserTest {
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
    void shouldReturnOk_whenUserHaveAnyCompany() throws Exception {
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
    void shouldReturnOk_whenUserHaveTwoCompany() throws Exception {
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
    void shouldReturnOk_whenUserDontHaveCompany() throws Exception {
         this.user = UserTestFactory.registerUser(mockMvc, "User", password);
        this.email = user.getEmail();
        this.token = UserTestFactory.login(mockMvc, email, password).getToken();
        this.companyApi = this.companyApi.withToken(token);
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        companyApi.withToken(null);
        companyApi.getCompaniesByCurrentUser()
                .andExpect(status().isUnauthorized());
    }

}
