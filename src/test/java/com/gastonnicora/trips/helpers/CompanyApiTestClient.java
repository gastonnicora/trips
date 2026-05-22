package com.gastonnicora.trips.helpers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import tools.jackson.databind.ObjectMapper;

public class CompanyApiTestClient {
    private final MockMvc mockMvc;
    private String token;

    private ObjectMapper objectMapper;

    public CompanyApiTestClient(MockMvc mockMvc,ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public CompanyApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public record CompanyCreateTest(
            String name,
            String email,
            String phone,
            Double latitude,
            Double longitude) {
    }

    public ResultActions createCompany(String name, String email, String phone, Double latitude, Double longitude)
            throws Exception {
        CompanyCreateTest body = new CompanyCreateTest(
                name,
                email,
                phone,
                latitude,
                longitude);

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(post("/api/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
    }

}
