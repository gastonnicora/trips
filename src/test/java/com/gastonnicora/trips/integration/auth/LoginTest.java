package com.gastonnicora.trips.integration.auth;

import java.util.stream.Stream;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    private UserDTO user;
    private final String name = "user";
    private final String pass = "goodPassword";
    private AuthApiTestClient authApi;
    private String email;

    @BeforeEach
    void setup() throws Exception {
        user = UserTestFactory.registerUser(mockMvc, name, pass);
        this.authApi = new AuthApiTestClient(mockMvc);
        email = user.getEmail();
    }

    // test login correcto
    @Test
    void shouldLoginSuccessfully() throws Exception {
        authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // test login correcto
    @Test
    void shouldLoginSuccessfullyWhenEmailHaveSpace() throws Exception {
        authApi.login(email + " ", pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // Test de comportamiento de campos inválidos
    @ParameterizedTest
    @MethodSource("invalid")
    void shouldReturnBadRequestWhenValidationFails(
            String email,
            String password,
            String expectedField) throws Exception {
        authApi.login(email, password)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(
                        jsonPath("$.errors.%s".formatted(expectedField))
                                .value(org.hamcrest.Matchers.notNullValue()));
    }

    // Test de comportamiento cuando el email y contraseñas no son correctos
    @ParameterizedTest
    @MethodSource("invalidCredentials")
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid(
            String email,
            String password) throws Exception {
        authApi.login(email, password)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(
                        jsonPath("$.message")
                                .value(org.hamcrest.Matchers.notNullValue()));
    }

    // Comprueba que se guarde correctamente la cookie de refreshToken
    @Test
    void shouldSetRefreshTokenCookieOnWebLogin() throws Exception {
        authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", org.hamcrest.Matchers.notNullValue()));

    }

    // Comprueba que se pase el refreshToken a android
    @Test
    void shouldReturnRefreshTokenInBodyForAndroidClients() throws Exception {
        authApi.loginWithAndroid(email, pass)
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    static Stream<Arguments> invalid() {
        return Stream.of(
                // email inválido
                Arguments.of("invalid-email", "goodPassword",
                        "email"),
                // email vació
                Arguments.of("", "goodPassword", "email"),
                // Contraseña de gran tamaño
                Arguments.of("test@test.com",
                        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penativehicle et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.",
                        "password"),
                // Contraseña vacia
                Arguments.of("test@test.com", "", "password"));
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                // Intento con email existente pero password errónea
                Arguments.of("user@test.com", "wrongPassword"),
                // Intento con email que no existe
                Arguments.of("nonexistent@test.com", "goodPassword"));
    }

}
