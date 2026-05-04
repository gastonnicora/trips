package com.gastonnicora.trips.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RefreshTokenTest {
    @Autowired
    private MockMvc mockMvc;

    private UserDTO user;
    private final String name = "user";
    private final String pass = "goodPassword";
    private AuthApiTestClient authApi;
    private String email;
    private String token;
    private String refreshToken;

    @BeforeEach
    void setup() throws Exception {
        user = UserTestFactory.registerUser(mockMvc, name, pass);
        email = user.getEmail();
        LoginResponse response = UserTestFactory.login(mockMvc, email, pass);
        token = response.getToken();
        refreshToken = response.getRefreshToken();

        this.authApi = new AuthApiTestClient(mockMvc).withToken(token);
    }

    @Test
    void shouldRefreshSuccessfully() throws Exception {
        authApi.refresh(refreshToken)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", org.hamcrest.Matchers.notNullValue()))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldRefreshSuccessfullyInAndroid() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        authApi.refreshAndroid(log.getRefreshToken())
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldRefreshFailsWhenRefreshTokenIsMissing() throws Exception {
        authApi.refresh("")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshFailsWhenRefreshTokenIsWrong() throws Exception {
        authApi.refresh("Wrong-Refresh-Token")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshFailsWhenRefreshTokenIsRevoque() throws Exception {
        authApi.refresh(refreshToken)
                .andExpect(status().isOk());
        authApi.refresh(refreshToken)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUseBodyEvenIfCookieExists_whenValid() throws Exception {

        mockMvc.perform(post("/api/auth/refresh")
                .cookie(new Cookie("refreshToken", "old-cookie-token"))
                .header("User-Agent", "JUnit-Test")
                .content("""
                            { "refreshToken": "%s" }
                        """.formatted(refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailWhenUserAgentDiffersFromOriginalLogin() throws Exception {

        mockMvc.perform(post("/api/auth/refresh")
                .header("User-Agent", "totally-different-browser")
                .content("""
                            { "refreshToken": "%s" }
                        """.formatted(refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailWhenIpDiffers() throws Exception {

        mockMvc.perform(post("/api/auth/refresh")
                .with(request -> {
                    request.setRemoteAddr("1.1.1.1");
                    return request;
                })
                .header("User-Agent", "JUnit-Test")
                .content("""
                            { "refreshToken": "%s" }
                        """.formatted(refreshToken))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
