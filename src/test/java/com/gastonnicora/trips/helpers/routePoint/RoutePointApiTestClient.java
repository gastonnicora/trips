package com.gastonnicora.trips.helpers.routePoint;

import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class RoutePointApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public RoutePointApiTestClient(
            MockMvc mockMvc,
            ObjectMapper objectMapper
                                  ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions createRoutePoint(
            UUID companyUuid,
            UUID routeUuid,
            String name,
            Double latitude,
            Double longitude
                                         ) throws Exception {

        String json = objectMapper.writeValueAsString(
                new RoutePointCreate(name, latitude, longitude)
                                                     );

        return createRoutePointRaw(companyUuid, routeUuid, json);
    }

    public ResultActions createRoutePointRaw(
            UUID companyUuid,
            UUID routeUuid,
            String json
                                            ) throws Exception {

        var request = post(
                "/api/companies/{companyUuid}/route/{routeUuid}/routePoint",
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

    public ResultActions getRoutePoint(UUID companyUuid, UUID routeUuid, UUID routePointUuid) throws Exception {

        var request = get(
                "/api/companies/{companyUuid}/route/{routeUuid}/routePoint/{uuid}",
                companyUuid,
                routeUuid,
                routePointUuid
                         )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions getRoutePoints(UUID companyUuid, UUID routeUuid) throws Exception {

        var request = get(
                "/api/companies/{companyUuid}/route/{routeUuid}/routePoint",
                companyUuid,
                routeUuid
                         )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions updateRoutePoint(
            UUID companyUuid,
            UUID routeUuid,
            UUID routePointUuid,
            String name,
            Double latitude,
            Double longitude
                                         ) throws Exception {

        String json = objectMapper.writeValueAsString(
                new RoutePointCreate(name, latitude, longitude)
                                                     );

        var request = put(
                "/api/companies/{companyUuid}/route/{routeUuid}/routePoint/{uuid}",
                companyUuid,
                routeUuid,
                routePointUuid
                         )
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json);

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public RoutePointApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }
}
