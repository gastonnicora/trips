package com.gastonnicora.trips.integration.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LogoutTest {
    @Autowired
    private MockMvc mockMvc;

    private UserDTOs user;
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
    void shouldLogoutSuccessfully() throws Exception {
        authApi.logout(refreshToken)
                .andExpect(status().isOk());
    }

    @Test
    void shouldLogoutSuccessfullyAndCookieClean() throws Exception {
        authApi.logout(refreshToken).andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void shouldLogoutSuccessfullyInAndroid() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        this.authApi = authApi.withToken(log.getToken());
        authApi.logoutAndroid(log.getRefreshToken()).andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    @Test
    void shouldLogoutFailsWhenTokenIsMissing() throws Exception {
        this.authApi = authApi.withToken("");
        authApi.logout(refreshToken).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldInvalidateRefreshTokenAfterLogoutInAndroid() throws Exception {
        LoginResponse login = UserTestFactory.loginWithAndroid(mockMvc, email, pass);

        // logout
        authApi.withToken(login.getToken())
                .logoutAndroid(login.getRefreshToken())
                .andExpect(status().isOk());

        // intento usar refresh token otra vez
        authApi.refreshAndroid(login.getRefreshToken())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldInvalidateRefreshTokenAfterLogout() throws Exception {

        // logout
        authApi
                .logout(refreshToken)
                .andExpect(status().isOk());

        // intento usar refresh token otra vez
        authApi.refresh(refreshToken)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutFailsWhenRefreshTokenIsMissing() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        this.authApi = authApi.withToken(log.getToken());
        authApi.logoutAndroid("").andExpect(status().isUnauthorized());
    }

    @Test
    void shouldStillAllowAccessWithOldAccessTokenAfterLogout() throws Exception {
        authApi.logout(refreshToken).andExpect(status().isOk());
        authApi.logout(refreshToken).andExpect(status().isUnauthorized());
    }

}
