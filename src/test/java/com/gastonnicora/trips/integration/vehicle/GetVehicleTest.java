package com.gastonnicora.trips.integration.vehicle;

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
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse.Address;
import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.VehicleApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetVehicleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyApiTestClient companyApi;

    private VehicleApiTestClient vehicleApi;

    private UserDTO user;
    private CompanyDTO company;

    private UUID vehicleUuid;

    private String token;

    @BeforeEach
    void setup() throws Exception {

        user = UserTestFactory.registerUser(
                mockMvc,
                "owner",
                "goodPassword"
        );

        LoginResponse login = UserTestFactory.login(mockMvc,
                user.getEmail(),
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

        MvcResult vehicleResponse = companyApi
                .createVehicle(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = vehicleResponse.getResponse().getContentAsString();
        vehicleUuid = objectMapper.readValue(responseJson, VehicleDTO.class).getUuid();
        user = UserTestFactory.registerUser(
                mockMvc,
                "owner",
                "goodPassword"
        );

        login = UserTestFactory.login(mockMvc,
                user.getEmail(),
                "goodPassword"
        );
        this.vehicleApi = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(login.getToken());
    }

    @Test
    void shouldGetVehicleSuccessfully() throws Exception {

        vehicleApi
                .getVehicle(
                        vehicleUuid
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").exists())
                .andExpect(jsonPath("$.plate").value("AA123BB"));
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {

        vehicleApi
                .getVehicle(
                        UUID.randomUUID()
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        VehicleApiTestClient apiWithoutToken
                = new VehicleApiTestClient(
                        mockMvc,
                        objectMapper
                );

        apiWithoutToken
                .getVehicle(
                        vehicleUuid
                )
                .andExpect(status().isUnauthorized());
    }

}
