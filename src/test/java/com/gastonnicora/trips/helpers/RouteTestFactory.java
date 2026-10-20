package com.gastonnicora.trips.helpers;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.enums.PricingType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RouteTestFactory {


    private final ObjectMapper objectMapper;
    private final RouteApiTestClient routeApi;

    public RouteTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token
                           ) {
        this.objectMapper = objectMapper;
        this.routeApi = new RouteApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    public RouteDTO createRoute(UUID companyUuid, String name, PricingType pricingType) throws Exception {
        MvcResult result = this.routeApi.createRoute(companyUuid, name, pricingType)
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                responseJson,
                RouteDTO.class
                                     );
    }
}
