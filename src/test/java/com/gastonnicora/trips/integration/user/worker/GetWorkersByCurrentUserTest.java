package com.gastonnicora.trips.integration.user.worker;

import java.util.Set;

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
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetWorkersByCurrentUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private UserApiTestClient userApi;

    private String token;
    private CompanyApiTestClient companyApi;

    private String email;
    private UserDTO user;
    private final String password = "goodPassword";

    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {
        String ownerToken = registerAndLoginUser("ownerComapany");
        createCompany();
        registerAndLoginUser("worker");
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
        createWorker(ownerToken);
    }

    private String registerAndLoginUser(String nombre) throws Exception {
        this.user = UserTestFactory.registerUser(mockMvc, nombre, this.password);
        this.email = user.getEmail();
        this.token = UserTestFactory.login(mockMvc, email, this.password).getToken();
        return this.token;
    }

    private void createCompany() throws Exception {
        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(new AddressResponse("calle falsa 123",
                        new Address("calle falsa", "123", "barrio", "ciudad", "departamento", "estado", "pais")));
        this.companyApi = new CompanyApiTestClient(mockMvc, objectMapper).withToken(this.token);
        MvcResult result = companyApi
                .createCompany("Good Name", "goodemail@mail.com", "+549112233445", -34.6037, -54.3816)
                .andExpect(status().isOk()).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        this.company = objectMapper.readValue(responseJson, CompanyDTO.class);
    }

    private void createWorker(String ownerToken) throws Exception {
        companyApi.withToken(ownerToken)
                .createWorker(company.getUuid(), user.getUuid(), Set.of(RoleCompany.DRIVER))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void shouldReturnOk_whenUserHaveAnyworker() throws Exception {
        userApi.getWorkersByCurrentUser()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value(user.getName()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andExpect(jsonPath("$.workers").isArray())
                .andExpect(jsonPath("$.workers").isNotEmpty())
                .andExpect(jsonPath("$.workers[0].roles").isArray())
                .andExpect(jsonPath("$.workers[0].roles[0]").value("DRIVER"));
    }
    
    @Test
    void shouldReturnNotFound_whenUserDontHaveAnyWorker() throws Exception {
        registerAndLoginUser("newUser");
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
        userApi.getWorkersByCurrentUser()
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        userApi.withToken(null);
        userApi.getWorkersByCurrentUser()
                .andExpect(status().isUnauthorized());
    }
}
