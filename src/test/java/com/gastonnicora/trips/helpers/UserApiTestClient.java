package com.gastonnicora.trips.helpers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;


public class UserApiTestClient {

    private final MockMvc mockMvc;
    private String token;

    public UserApiTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public UserApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public ResultActions register(String name, String lastname, String email, String password,String confirmPassword) throws Exception {
        return mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(name, lastname, email, password, confirmPassword)));
    }

    public ResultActions update(String name, String lastname, String email) throws Exception {
        return mockMvc.perform(put("/api/user")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(name, lastname, email, null, null)));
    }
}