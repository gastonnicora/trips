package com.gastonnicora.trips.helpers;

import com.gastonnicora.trips.dtos.request.worker.WorkerCreate;
import com.gastonnicora.trips.enums.RoleCompany;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class WorkerApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public WorkerApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions createWorker(UUID companyUuid, UUID userUuid, Set<RoleCompany> roles)
            throws Exception {

        WorkerCreateTest body = new WorkerCreateTest(userUuid, roles);

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(post("/api/companies/" + companyUuid + "/worker")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
    }

    public ResultActions deleteWorker(UUID companyUuid, UUID userUuid)
            throws Exception {

        return mockMvc.perform(delete(
                "/api/companies/" + companyUuid + "/worker/" + userUuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getWorkersByCompany(UUID uuid) throws Exception {
        return mockMvc.perform(get("/api/companies/" + uuid + "/workers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions getWorkersByCurrentUser() throws Exception {
        return mockMvc.perform(get("/api/users/workers")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions getWorkersByUser(String uuid) throws Exception {
        return mockMvc.perform(get("/api/users/workers/" + uuid + "/workers")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions updateWorkerRoles(
            UUID companyUuid,
            UUID userUuid,
            Set<RoleCompany> roles) throws Exception {

        WorkerCreate body = new WorkerCreate(
                userUuid,
                roles
        );

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(put(
                "/api/companies/" + companyUuid + "/worker/" + userUuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
    }

    public WorkerApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public record WorkerCreateTest(
            UUID userUuid,
            Set<RoleCompany> roles) {

    }

}
