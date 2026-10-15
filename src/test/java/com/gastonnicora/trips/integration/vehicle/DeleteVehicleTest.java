package com.gastonnicora.trips.integration.vehicle;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.helpers.CompanyTestFactory;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.gastonnicora.trips.helpers.VehicleApiTestClient;
import com.gastonnicora.trips.helpers.VehicleTestFactory;
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
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteVehicleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private VehicleApiTestClient vehicleApi;

    private CompanyDTO company;

    private UUID vehicleUuid;

    private String token;

    @BeforeEach
    void setup() throws Exception {

        token = UserTestFactory.registerAndLogin(mockMvc);


        vehicleApi = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(token);


        company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();

        vehicleUuid = new VehicleTestFactory(mockMvc, objectMapper, token).createVehicle(company.getUuid()).getUuid();

    }

    @Test
    void shouldDeleteVehicleSuccessfully() throws Exception {

        vehicleApi
                .deleteVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isOk());

        vehicleApi
                .deleteVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {

        vehicleApi
                .deleteVehicle(
                        UUID.randomUUID(),
                        vehicleUuid
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotHavePermission() throws Exception {

        String token = UserTestFactory.registerAndLogin(mockMvc);

        VehicleApiTestClient anotherUserApi
                = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(token);

        anotherUserApi
                .deleteVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {

        vehicleApi
                .deleteVehicle(
                        company.getUuid(),
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
                .deleteVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isUnauthorized());
    }
}
