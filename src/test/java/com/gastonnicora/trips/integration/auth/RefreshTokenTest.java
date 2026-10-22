package com.gastonnicora.trips.integration.auth;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.dtos.response.auth.RefreshResponse;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RefreshTokenTest {

    private final String pass = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    private AuthApiTestClient authApi;
    private String email;
    private String refreshToken;

    @BeforeEach
    void setup() throws Exception {
        String name = "user";
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, pass);
        email = user.getEmail();
        LoginResponse response = UserTestFactory.login(mockMvc, email, pass);
        String token = response.getToken();
        refreshToken = response.getRefreshToken();

        this.authApi = new AuthApiTestClient(mockMvc).withToken(token);
    }

    @Test
    void shouldRefreshSuccessfully() throws Exception {
        authApi.refresh(refreshToken)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", notNullValue()))
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
    void shouldReturnUnauthorizedWhenCurrentIpDiffersFromOriginalLogin() throws Exception {

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

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsInvalid() throws Exception {
        authApi.refresh("Wrong-Refresh-Token")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsMissing() throws Exception {
        authApi.refresh("")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsRevoked() throws Exception {
        authApi.refresh(refreshToken)
                .andExpect(status().isOk());
        // Intentar usar el mismo token por segunda vez (detección de reutilización)
        authApi.refresh(refreshToken)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUserAgentDiffersFromOriginalLogin() throws Exception {

        mockMvc.perform(post("/api/auth/refresh")
                                .header("User-Agent", "totally-different-browser")
                                .content("""
                                                     { "refreshToken": "%s" }
                                                 """.formatted(refreshToken))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUsableRefreshTokenInBodyWhenRefreshingInAndroid() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);

        MvcResult result = authApi.refreshAndroid(log.getRefreshToken())
                .andExpect(status().isOk())
                .andReturn();
        RefreshResponse refreshed = new ObjectMapper()
                .readValue(result.getResponse().getContentAsString(), RefreshResponse.class);

        // Debe ser un refresh token nuevo (UUID), no el access token ni el anterior
        assertNotEquals(refreshed.getToken(), refreshed.getRefreshToken());
        assertNotEquals(log.getRefreshToken(), refreshed.getRefreshToken());
        assertDoesNotThrow(() -> UUID.fromString(refreshed.getRefreshToken()));

        // Y tiene que servir para renovar otra vez
        authApi.refreshAndroid(refreshed.getRefreshToken())
                .andExpect(status().isOk());
    }

    @Test
    void shouldUseBodyRefreshTokenWhenCookieAlsoExists() throws Exception {

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

}
