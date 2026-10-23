package com.gastonnicora.trips.helpers.pricePerKilometer;

import com.gastonnicora.trips.dtos.request.pricePerKilometer.PricePerKilometerCreate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class PricePerKilometerApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public PricePerKilometerApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public PricePerKilometerApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions createPricePerKilometer(
            UUID companyUuid,
            UUID routeUuid,
            BigDecimal baseKm,
            BigDecimal basePrice,
            BigDecimal pricePerKm
                                                ) throws Exception {

        String json = objectMapper.writeValueAsString(
                new PricePerKilometerCreate(baseKm, basePrice, pricePerKm)
                                                     );

        return createPricePerKilometerRaw(companyUuid, routeUuid, json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado.
     */
    public ResultActions createPricePerKilometerRaw(UUID companyUuid, UUID routeUuid, String json)
            throws Exception {

        var request = post(
                "/api/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer",
                companyUuid,
                routeUuid
                          )
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json);

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions getPricePerKilometer(UUID companyUuid, UUID routeUuid, UUID uuid) throws Exception {

        var request = get(
                "/api/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer/{uuid}",
                companyUuid,
                routeUuid,
                uuid
                         )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }
}