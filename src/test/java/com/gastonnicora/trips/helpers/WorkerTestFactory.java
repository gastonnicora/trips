package com.gastonnicora.trips.helpers;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.enums.RoleCompany;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkerTestFactory {

    private final WorkerApiTestClient workerApi;
    private final ObjectMapper objectMapper;

    public WorkerTestFactory(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String token) {
        this.objectMapper = objectMapper;
        this.workerApi = new WorkerApiTestClient(mockMvc, objectMapper).withToken(token);
    }

    public WorkerDTO createWorker(UUID companyUuid, UUID userUuid, Set<RoleCompany> roles) throws Exception {
        MvcResult result = this.workerApi.createWorker(companyUuid, userUuid, roles)
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result
                .getResponse()
                .getContentAsString();

        WorkerDTO vehicle = objectMapper.readValue(
                responseJson,
                WorkerDTO.class
        );

        return vehicle;
    }
}
