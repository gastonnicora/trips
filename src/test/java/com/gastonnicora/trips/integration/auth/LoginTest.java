package com.gastonnicora.trips.integration.auth;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

    private final String pass = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    private AuthApiTestClient authApi;
    private String email;

    static Stream<Arguments> invalid() {
        return Stream.of(
                Arguments.of(
                        "invalid-email", "goodPassword",
                        "email"
                            ),
                Arguments.of("", "goodPassword", "email"),
                Arguments.of(
                        "test@test.com",
                        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penativehicle et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.",
                        "password"
                            ),
                Arguments.of("test@test.com", "", "password")
                        );
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of("user@test.com", "wrongPassword"),
                Arguments.of("nonexistent@test.com", "goodPassword")
                        );
    }

    @BeforeEach
    void setup() throws Exception {
        String name = "user";
        UserDTO user = UserTestFactory.registerUser(mockMvc, name, pass);
        this.authApi = new AuthApiTestClient(mockMvc);
        email = user.getEmail();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldLoginSuccessfullyWhenEmailHaveSpace() throws Exception {
        authApi.login(email + " ", pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @ParameterizedTest
    @MethodSource("invalid")
    void shouldReturnBadRequestWhenValidationFails(
            String email,
            String password,
            String expectedField
                                                  ) throws Exception {
        authApi.login(email, password)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(
                        jsonPath("$.errors.%s".formatted(expectedField))
                                .value(notNullValue()));
    }

    @Test
    void shouldReturnRefreshTokenInBodyForAndroidClients() throws Exception {
        authApi.loginWithAndroid(email, pass)
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidCredentials")
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid(
            String email,
            String password
                                                          ) throws Exception {
        authApi.login(email, password)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(
                        jsonPath("$.message")
                                .value(notNullValue()));
    }

    @Test
    void shouldSetRefreshTokenCookieOnWebLogin() throws Exception {
        authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", notNullValue()));

    }

}
