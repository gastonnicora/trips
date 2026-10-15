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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetVehicleTest {

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

        this.token = UserTestFactory.registerAndLogin(mockMvc);

        company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();

        vehicleUuid = new VehicleTestFactory(mockMvc, objectMapper, token).createVehicle(company.getUuid()).getUuid();

        this.vehicleApi = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(token);
    }

    @Test
    void shouldReturnForbiddenWhenVehicleBelongsToAnotherCompany() throws Exception {

        CompanyDTO anotherCompany = new CompanyTestFactory(
                mockMvc,
                objectMapper,
                token,
                geocodingService
        ).createCompany();

        vehicleApi
                .getVehicle(
                        anotherCompany.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {

        vehicleApi
                .getVehicle(
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
                .getVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnVehicleSuccessfully() throws Exception {

        vehicleApi
                .getVehicle(
                        company.getUuid(),
                        vehicleUuid
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").exists())
                .andExpect(jsonPath("$.plate").value("AA123BB"));
    }

}
