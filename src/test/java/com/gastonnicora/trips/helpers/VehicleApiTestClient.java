package com.gastonnicora.trips.helpers;

import java.util.UUID;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import tools.jackson.databind.ObjectMapper;


public class VehicleApiTestClient {

    private final MockMvc mockMvc;
    private String token;

    private final ObjectMapper objectMapper;

    public VehicleApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public VehicleApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions getVehicle(UUID uuid)
            throws Exception {

        return mockMvc.perform(get("/api/vehicles/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

}
