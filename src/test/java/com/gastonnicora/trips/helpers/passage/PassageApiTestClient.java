package com.gastonnicora.trips.helpers.passage;

import com.gastonnicora.trips.dtos.request.passage.PassageCreate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class PassageApiTestClient {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;

    public PassageApiTestClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public PassageApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions createPassage(PassageCreate passageCreate) throws Exception {
        String json = objectMapper.writeValueAsString(passageCreate);
        return createPassageRaw(json);
    }

    /**
     * Envía el body tal cual, útil para probar JSON mal formado.
     */
    public ResultActions createPassageRaw(String json) throws Exception {
        var request = post("/api/passages")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(json);

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        return mockMvc.perform(request);
    }
}