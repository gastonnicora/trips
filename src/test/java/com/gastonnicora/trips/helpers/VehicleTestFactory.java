package com.gastonnicora.trips.helpers;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VehicleTestFactory {

    private final VehicleApiTestClient vehicleApi;
    private final ObjectMapper objectMapper;

    public VehicleTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token) {
        this.objectMapper = objectMapper;
        this.vehicleApi = new VehicleApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    public VehicleDTO createVehicle(UUID companyUuid) throws Exception {
        MvcResult result = this.vehicleApi.createVehicle(companyUuid, "AA123BB",
                        "Mercedes Benz",
                        50)
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                responseJson,
                VehicleDTO.class
                                     );
    }

}
