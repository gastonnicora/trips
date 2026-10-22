package com.gastonnicora.trips.integration.vehicle;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.helpers.company.CompanyTestFactory;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import com.gastonnicora.trips.helpers.vehicle.VehicleApiTestClient;
import com.gastonnicora.trips.helpers.vehicle.VehicleTestFactory;
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
class UpdateVehicleTest {

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

        // crea un vehículo con patente "AA123BB", modelo "Mercedes Benz" y capacidad 50
        vehicleUuid = new VehicleTestFactory(mockMvc, objectMapper, token).createVehicle(company.getUuid()).getUuid();
    }

    @Test
    void shouldNormalizePlateWhenUpdatingVehicle() throws Exception {

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "cc 456 dd",
                        "Iveco",
                        30
                              )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate")
                                   .value("CC456DD"));
    }

    @Test
    void shouldReturnBadRequestWhenVehicleDataIsInvalid() throws Exception {

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "",
                        "Iveco",
                        30
                              )
                .andExpect(status().isBadRequest());

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        0
                              )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenPlateAlreadyExists() throws Exception {

        vehicleApi
                .createVehicle(
                        company.getUuid(),
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isOk());

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Mercedes Benz",
                        50
                              )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnForbiddenWhenCompanyDoesNotExist() throws Exception {

        vehicleApi
                .updateVehicle(
                        UUID.randomUUID(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotHavePermission() throws Exception {

        String anotherToken = UserTestFactory.registerAndLogin(mockMvc);

        VehicleApiTestClient anotherUserApi
                = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(anotherToken);

        anotherUserApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        UUID.randomUUID(),
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenVehicleIsInactive() throws Exception {

        vehicleApi
                .deleteVehicle(company.getUuid(), vehicleUuid)
                .andExpect(status().isOk());

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        30
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
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateVehicleSuccessfully() throws Exception {

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "CC456DD",
                        "Iveco",
                        30
                              )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid")
                                   .value(vehicleUuid.toString()))
                .andExpect(jsonPath("$.company.uuid")
                                   .value(company.getUuid().toString()))
                .andExpect(jsonPath("$.plate")
                                   .value("CC456DD"))
                .andExpect(jsonPath("$.model")
                                   .value("Iveco"))
                .andExpect(jsonPath("$.capacity")
                                   .value(30))
                .andExpect(jsonPath("$.active")
                                   .value(true));

        // el cambio quedó persistido
        vehicleApi
                .getVehicle(company.getUuid(), vehicleUuid)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value("CC456DD"))
                .andExpect(jsonPath("$.model").value("Iveco"))
                .andExpect(jsonPath("$.capacity").value(30));
    }

    @Test
    void shouldUpdateVehicleSuccessfullyWhenPlateDoesNotChange() throws Exception {

        vehicleApi
                .updateVehicle(
                        company.getUuid(),
                        vehicleUuid,
                        "AA123BB",
                        "Iveco",
                        30
                              )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate")
                                   .value("AA123BB"))
                .andExpect(jsonPath("$.model")
                                   .value("Iveco"))
                .andExpect(jsonPath("$.capacity")
                                   .value(30));
    }
}