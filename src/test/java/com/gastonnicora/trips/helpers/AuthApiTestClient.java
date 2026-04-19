package com.gastonnicora.trips.helpers;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AuthApiTestClient {

    private final MockMvc mockMvc;
    private String token;

    public AuthApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

    public AuthApiTestClient(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
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
                        """.formatted(email, password))); // TODO Cambiar por android
    }

    
    public ResultActions logout(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .header("Authorization", "Bearer " + token)
                .content("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)));
    }

    public ResultActions logoutAndroid(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "okhttp/4.9.0 (Android)")
                .header("Authorization", "Bearer " + token)
                .content("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)));
    }

    public ResultActions refreshAndroid(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "okhttp/4.9.0 (Android)")
                .header("Authorization", "Bearer " + token)
                .content("""
                        {"refreshToken":"%s"}
                        """.formatted(refreshToken)));
    }
    public ResultActions refresh() throws Exception {
        return mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .header("Authorization", "Bearer " + token));
    }

}