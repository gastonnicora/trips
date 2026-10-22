package com.gastonnicora.trips.helpers;

import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthApiTestClient {

    private final MockMvc mockMvc;
    private String token;

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

    public ResultActions loginWithAndroid(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .header("User-Agent", "okhttp/4.9.0 (Android)")
                                       .content("""
                                                        {"email":"%s","password":"%s"}
                                                        """.formatted(email, password)));
    }

    public ResultActions logout(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/logout")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .cookie(new Cookie("refreshToken", refreshToken))
                                       .header("User-Agent", "JUnit-Test")
                                       .header("Authorization", "Bearer " + token));
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

    public ResultActions refresh(String refreshToken) throws Exception {
        return mockMvc.perform(post("/api/auth/refresh")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .cookie(new Cookie("refreshToken", refreshToken))
                                       .header("User-Agent", "JUnit-Test")
                                       .header("Authorization", "Bearer " + token));
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

    public AuthApiTestClient withToken(String token) {
        this.token = token;
        return this;
    }

}
