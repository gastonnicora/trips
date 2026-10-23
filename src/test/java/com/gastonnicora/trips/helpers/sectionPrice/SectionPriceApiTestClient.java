package com.gastonnicora.trips.helpers.sectionPrice;

import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceCreate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class SectionPriceApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public SectionPriceApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public SectionPriceApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions createSectionPrice(
            UUID companyUuid,
            UUID routeUuid,
            UUID fromPoint,
            UUID toPoint,
            BigDecimal price
                                           ) throws Exception {

        String json = objectMapper.writeValueAsString(
                new SectionPriceCreate(fromPoint, toPoint, price)
                                                     );

        return createSectionPriceRaw(companyUuid, routeUuid, json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado.
     */
    public ResultActions createSectionPriceRaw(UUID companyUuid, UUID routeUuid, String json)
            throws Exception {

        var request = post(
                "/api/companies/{companyUuid}/route/{routeUuid}/sectionPrice",
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
}