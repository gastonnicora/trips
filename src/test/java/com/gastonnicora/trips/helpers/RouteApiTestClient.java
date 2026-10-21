package com.gastonnicora.trips.helpers;

import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.enums.PricingType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RouteApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public RouteApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions createRoute(
            UUID companyUuid,
            String name,
            PricingType pricingType
                                    ) throws Exception {

        String json = objectMapper.writeValueAsString(new RouteCreate(name, pricingType));

        return createRouteRaw(companyUuid, json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado o con valores
     * que no se pueden construir desde {@link RouteCreate}.
     */
    public ResultActions createRouteRaw(UUID companyUuid, String json) throws Exception {

        return mockMvc.perform(post("/api/companies/" + companyUuid + "/route")
                                       .header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("User-Agent", "JUnit-Test")
                                       .content(json));
    }

    public ResultActions getRoute(UUID companyUuid, UUID routeUuid) throws Exception {

        return mockMvc.perform(get("/api/companies/" + companyUuid + "/route/" + routeUuid)
                                       .header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getRoutes(UUID companyUuid) throws Exception {

        return mockMvc.perform(get("/api/companies/" + companyUuid + "/routes")
                                       .header("Authorization", "Bearer " + token)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("User-Agent", "JUnit-Test"));
    }

    public RouteApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }
}
