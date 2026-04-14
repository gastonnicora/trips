package com.gastonnicora.trips.helpers;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AuthApiTestClient {

    private final MockMvc mockMvc;

    public AuthApiTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password)));
    }

    public ResultActions loginWithUserAgent(String email, String password, String userAgent) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", userAgent)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, password)));
    }
}