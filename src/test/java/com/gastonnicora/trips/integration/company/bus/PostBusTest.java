package com.gastonnicora.trips.integration.company.bus;

import java.util.UUID;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
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
class PostBusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyApiTestClient companyApi;

    private UserDTO owner;
    private CompanyDTO company;

    @BeforeEach
    void setup() throws Exception {

        owner = UserTestFactory.registerUser(
                mockMvc,
                "owner",
                "goodPassword"
        );

        LoginResponse login = UserTestFactory.login(
                mockMvc,
                owner.getEmail(),
                "goodPassword"
        );

        companyApi = new CompanyApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(login.getToken());

        when(geocodingService.obtenerDireccion(anyDouble(), anyDouble()))
                .thenReturn(
                        new AddressResponse(
                                "calle falsa 123",
                                new Address(
                                        "calle falsa",
                                        "123",
                                        "barrio",
                                        "ciudad",
                                        "departamento",
                                        "estado",
                                        "pais"
                                )
                        )
                );

        String response = companyApi
                .createCompany(
                        "Test Company",
                        "company_" + System.currentTimeMillis() + "@test.com",
                        "123456789",
                        -34.6037,
                        -58.3816
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        company = objectMapper.readValue(
                response,
                CompanyDTO.class
        );
    }

    @Test
    void shouldCreateBusSuccessfully() throws Exception {


        companyApi
                .createBus(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.company.uuid")
                        .value(company.getUuid().toString()))
                .andExpect(jsonPath("$.plate")
                        .value("AA123BB"))
                .andExpect(jsonPath("$.model")
                        .value("Mercedes Benz"))
                .andExpect(jsonPath("$.capacity")
                        .value(50))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldReturnConflictWhenPlateAlreadyExists() throws Exception {

        companyApi
                .createBus(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isOk());

        companyApi
                .createBus(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {


        companyApi
                .createBus(
                        UUID.randomUUID(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        CompanyApiTestClient apiWithoutToken =
                new CompanyApiTestClient(
                        mockMvc,
                        objectMapper
                );


        apiWithoutToken
                .createBus(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenBusDataIsInvalid() throws Exception {

        companyApi
                .createBus(
                        company.getUuid(),
                        "",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isBadRequest());
    }
}
