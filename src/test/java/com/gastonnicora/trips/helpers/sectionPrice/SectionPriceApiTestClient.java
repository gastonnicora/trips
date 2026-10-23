package com.gastonnicora.trips.helpers.sectionPrice;

import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceCreate;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceUpdate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class SectionPriceApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public SectionPriceApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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

    public ResultActions deleteSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid)
            throws Exception {

        var request = delete(
                "/api/companies/{companyUuid}/route/{routeUuid}/sectionPrice/{uuid}",
                companyUuid,
                routeUuid,
                sectionUuid
                            )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions getSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid)
            throws Exception {

        var request = get(
                "/api/companies/{companyUuid}/route/{routeUuid}/sectionPrice/{uuid}",
                companyUuid,
                routeUuid,
                sectionUuid
                         )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions getSectionPrices(UUID companyUuid, UUID routeUuid) throws Exception {

        var request = get(
                "/api/companies/{companyUuid}/route/{routeUuid}/sectionPrice",
                companyUuid,
                routeUuid
                         )
                .header("User-Agent", "JUnit-Test");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public ResultActions updateSectionPrice(UUID companyUuid, UUID routeUuid, UUID sectionUuid, BigDecimal price)
            throws Exception {

        String json = objectMapper.writeValueAsString(new SectionPriceUpdate(price));

        return updateSectionPriceRaw(companyUuid, routeUuid, sectionUuid, json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado.
     */
    public ResultActions updateSectionPriceRaw(UUID companyUuid, UUID routeUuid, UUID sectionUuid, String json)
            throws Exception {

        var request = put(
                "/api/companies/{companyUuid}/route/{routeUuid}/sectionPrice/{uuid}",
                companyUuid,
                routeUuid,
                sectionUuid
                         )
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json);

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }

    public SectionPriceApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }
}