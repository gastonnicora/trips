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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetVehiclesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeocodingService geocodingService;

    private VehicleApiTestClient vehicleApi;

    private CompanyDTO company;

    private String token;


    @BeforeEach
    void setup() throws Exception {

        token = UserTestFactory.registerAndLogin(mockMvc);

        company = new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();

         new VehicleTestFactory(mockMvc, objectMapper, token).createVehicle(company.getUuid());

        this.vehicleApi = new VehicleApiTestClient(
                mockMvc,
                objectMapper
        ).withToken(token);
    }


    @Test
    void shouldReturnNotFoundWhenCompanyDontHaveVehicles() throws Exception {

        CompanyDTO company2= new CompanyTestFactory(mockMvc, objectMapper, token, geocodingService).createCompany();
        vehicleApi
                .getVehicles(
                        company2.getUuid()
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
                .getVehicles(
                        company.getUuid()
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnVehiclesSuccessfully() throws Exception {

        vehicleApi
                .getVehicles(
                        company.getUuid()
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].plate").exists())
                .andExpect(jsonPath("$.[*].plate").value("AA123BB"));
    }


}
