package com.gastonnicora.trips.helpers.routePoint;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoutePointTestFactory {


    private final ObjectMapper objectMapper;
    private final RoutePointApiTestClient routePointApi;

    public RoutePointTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token
                                ) {
        this.objectMapper = objectMapper;
        this.routePointApi = new RoutePointApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    public RouteDTO createRoute(UUID companyUuid, UUID routeUuid, String name, double lat, double lon)
            throws Exception {
        MvcResult result = this.routePointApi.createRoutePoint(companyUuid, routeUuid, name, lat, lon)
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
