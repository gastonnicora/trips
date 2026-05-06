package com.gastonnicora.trips.helpers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.gastonnicora.trips.enums.Role;

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

    public ResultActions register(String name, String lastname, String email, String password, String confirmPassword)
            throws Exception {
        return mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(name, lastname, email, password, confirmPassword)));
    }

    public ResultActions update(String name, String lastname, String email) throws Exception {
        return mockMvc.perform(put("/api/users")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(name, lastname, email, null, null)));
    }

    public ResultActions updatePassword(String passwordOld, String password, String confirmPassword) throws Exception {
        return mockMvc.perform(put("/api/users/me/password")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJsonPutPass(passwordOld, password, confirmPassword)));
    }

    public ResultActions deleteCurrentUser() throws Exception {
        return mockMvc.perform(delete("/api/users")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions deleteCurrentUserWithUserAgent(String userAgent) throws Exception {
        return mockMvc.perform(delete("/api/users")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", userAgent)
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions getMe() throws Exception {
        return mockMvc.perform(get("/api/users/me")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions getUser(String uuid) throws Exception {
        return mockMvc.perform(get("/api/users/" + uuid)
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions getUsers() throws Exception {
        return mockMvc.perform(get("/api/users")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions changeRole(String uuid, Set<Role> role) throws Exception {
        return mockMvc.perform(put("/api/users/" + uuid + "/role")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON)

                .content("""
                        {
                        "roles": %s
                        }""".formatted(setToJson(role))));
    }

    private String setToJson(Set<Role> roles) {
        return roles.stream()
                .map(role -> "\"" + role.name() + "\"")
                .collect(Collectors.joining(", ", "[", "]"));
    }
}