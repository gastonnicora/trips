package com.gastonnicora.trips.helpers;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.dtos.request.company.WorkerCreate;
import com.gastonnicora.trips.enums.RoleCompany;

import tools.jackson.databind.ObjectMapper;

public class CompanyApiTestClient {

    private final MockMvc mockMvc;
    private String token;

    private final ObjectMapper objectMapper;

    public CompanyApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
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

    public ResultActions getCompany(UUID uuid)
            throws Exception {

        return mockMvc.perform(get("/api/companies/" + uuid)
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

    public ResultActions getCompaniesByCurrentUser()
            throws Exception {

        return mockMvc.perform(get("/api/companies/me")
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

    public ResultActions deleteCompany(UUID uuid)
            throws Exception {

        return mockMvc.perform(delete("/api/companies/" + uuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public record WorkerCreateTest(
            UUID userUuid,
            Set<RoleCompany> roles) {

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

    public ResultActions getWorkersByCompany(UUID uuid) throws Exception {
        return mockMvc.perform(get("/api/companies/" + uuid + "/workers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
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

    public ResultActions deleteWorker(UUID companyUuid, UUID userUuid)
            throws Exception {

        return mockMvc.perform(delete(
                "/api/companies/" + companyUuid + "/worker/" + userUuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

    public ResultActions createVehicle(UUID companyUuid, String plate, String model, Integer capacity) throws Exception {

        VehicleCreate body = new VehicleCreate(
                plate,
                model,
                capacity
        );

        String json = objectMapper.writeValueAsString(body);

        return mockMvc.perform(post("/api/companies/" + companyUuid + "/vehicle")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json));
    }
    public ResultActions deleteVehicle(UUID companyUuid, UUID vehicleUuid)
            throws Exception {

        return mockMvc.perform(delete(
                "/api/companies/" + companyUuid + "/vehicle/" + vehicleUuid)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test"));
    }

}
