package com.gastonnicora.trips.helpers;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class CompanyApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public CompanyApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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

    public ResultActions deleteCompany(UUID uuid)
            throws Exception {

        return mockMvc.perform(delete("/api/companies/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getCompaniesByCurrentUser()
            throws Exception {

        return mockMvc.perform(get("/api/companies/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getCompaniesByOwner(UUID uuid)
            throws Exception {

        return mockMvc.perform(get("/api/companies/owner/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getCompany(UUID uuid)
            throws Exception {

        return mockMvc.perform(get("/api/companies/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions updateCompany(UUID uuid, String name, String email, String phone, Double latitude,
                                       Double longitude)
            throws Exception {
        CompanyCreateTest body = new CompanyCreateTest(
                name,
                email,
                phone,
                latitude,
                longitude);

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(put("/api/companies/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
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

    public ResultActions getCompanies()
            throws Exception {

        return mockMvc.perform(get("/api/companies/")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

}
