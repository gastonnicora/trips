package com.gastonnicora.trips.helpers.pricePerKilometer;

import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PricePerKilometerTestFactory {

    private final ObjectMapper objectMapper;
    private final PricePerKilometerApiTestClient pricePerKilometerApi;

    public PricePerKilometerTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token
                                       ) {
        this.objectMapper = objectMapper;
        this.pricePerKilometerApi = new PricePerKilometerApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    public PricePerKilometerDTO createPricePerKilometer(
            UUID companyUuid,
            UUID routeUuid,
            BigDecimal baseKm,
            BigDecimal basePrice,
            BigDecimal pricePerKm
                                                       ) throws Exception {

        MvcResult result = this.pricePerKilometerApi
                .createPricePerKilometer(companyUuid, routeUuid, baseKm, basePrice, pricePerKm)
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                responseJson,
                PricePerKilometerDTO.class
                                     );
    }
}