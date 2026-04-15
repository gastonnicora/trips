package com.gastonnicora.trips.integration.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

        @Autowired
        private MockMvc mockMvc;

        private UserDTOs user;
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

        //Test de comportamiento de campos invalidos
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
        //Test de comportamiento cuando el email y contraseñas no son correctos
        @ParameterizedTest
        @MethodSource("invalidCredentials")
        void shouldReturnUnauthorizedWhenCredentialsAreInvalid(
                        String email,
                        String password) throws Exception {
                UserApiTestClient userApi= new UserApiTestClient(mockMvc);
                userApi.register("Juan","Nicora","test@test.com","goodPassword","goodPassword");
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

        // Comprueba que se pase el refreshtoken a android
        @Test
        void shouldReturnRefreshTokenInBodyForAndroidClients() throws Exception {
                authApi.loginWithUserAgent(email, pass, "okhttp/4.9.0 (Android)")
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

                                // email con espacio
                                Arguments.of("test@test.com ", "goodPassword", 
                                                "email"),//TODO hacer trim de email y sacar test

                                // Contraseña de gran tamaño
                                Arguments.of("test@test.com",
                                                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.",
                                                "password"),
                                // Contraseña vacia
                                Arguments.of("test@test.com", "", "password"));
        }

        static Stream<Arguments> invalidCredentials() {
                return Stream.of(
                                // Contraseña invalida
                                Arguments.of("test@test.com", "wrongPassword"),
                                // email incorrecto
                                Arguments.of("wrongEmail@test.com", "goodPassword"));
        }

}
