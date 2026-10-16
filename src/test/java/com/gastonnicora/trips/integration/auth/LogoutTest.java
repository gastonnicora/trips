package com.gastonnicora.trips.integration.auth;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LogoutTest {

    private final String name = "user";
    private final String pass = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    private UserDTO user;
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
    void shouldClearRefreshCookieWithTheSamePathUsedToCreateIt() throws Exception {
        // El navegador solo borra la cookie si nombre y path coinciden con los de la original
        MvcResult login = authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();
        jakarta.servlet.http.Cookie created = login.getResponse().getCookie("refreshToken");

        MvcResult logout = authApi.logout(created.getValue())
                .andExpect(status().isOk())
                .andReturn();
        jakarta.servlet.http.Cookie cleared = logout.getResponse().getCookie("refreshToken");

        assertEquals(created.getPath(), cleared.getPath());
        assertEquals(0, cleared.getMaxAge());
    }

    @Test
    void shouldClearRefreshTokenCookieOnLogout() throws Exception {
        authApi.logout(refreshToken).andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void shouldInvalidateRefreshTokenAfterLogout() throws Exception {
        authApi
                .logout(refreshToken)
                .andExpect(status().isOk());
        authApi.refresh(refreshToken)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldInvalidateRefreshTokenAfterLogoutInAndroid() throws Exception {
        LoginResponse login = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        authApi.withToken(login.getToken())
                .logoutAndroid(login.getRefreshToken())
                .andExpect(status().isOk());
        authApi.refreshAndroid(login.getRefreshToken())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        authApi.logout(refreshToken)
                .andExpect(status().isOk());
    }

    @Test
    void shouldLogoutSuccessfullyForAndroidClient() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        this.authApi = authApi.withToken(log.getToken());
        authApi.logoutAndroid(log.getRefreshToken()).andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
        this.authApi = authApi.withToken("");
        authApi.logout(refreshToken).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenLoggingOutWithRevokedRefreshToken() throws Exception {
        // Primer logout exitoso
        authApi.logout(refreshToken).andExpect(status().isOk());
        // El segundo intento con el mismo token debe fallar porque la sesión ya fue
        // invalidada en el filtro
        authApi.logout(refreshToken).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenIsMissing() throws Exception {
        LoginResponse log = UserTestFactory.loginWithAndroid(mockMvc, email, pass);
        this.authApi = authApi.withToken(log.getToken());
        authApi.logoutAndroid("").andExpect(status().isUnauthorized());
    }

}
