package com.gastonnicora.trips.integration.vehicle;

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

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.VehicleApiTestClient;
import com.gastonnicora.trips.services.GeocodingService;

import jakarta.transaction.Transactional;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateVehicleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private CompanyDTO company;
    private String token;
    private VehicleApiTestClient vehicleApi;

    @BeforeEach
    void setup() throws Exception {

        token = UserTestFactory.registerAndLogin(mockMvc);

        company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();

        vehicleApi = new VehicleApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    @Test
    void shouldCreateVehicleSuccessfully() throws Exception {

        vehicleApi
                .createVehicle(
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

        vehicleApi
                .createVehicle(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isOk());

        vehicleApi
                .createVehicle(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {

        vehicleApi
                .createVehicle(
                        UUID.randomUUID(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {

        VehicleApiTestClient apiWithoutToken
                = new VehicleApiTestClient(
                        mockMvc,
                        objectMapper
                );

        apiWithoutToken
                .createVehicle(
                        company.getUuid(),
                        "AA123BB",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenVehicleDataIsInvalid() throws Exception {

        vehicleApi
                .createVehicle(
                        company.getUuid(),
                        "",
                        "Mercedes Benz",
                        50
                )
                .andExpect(status().isBadRequest());
    }
}
