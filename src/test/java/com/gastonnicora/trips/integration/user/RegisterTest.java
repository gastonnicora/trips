package com.gastonnicora.trips.integration.user;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    UserApiTestClient apiUser;

    @BeforeEach
    void setup() {
        this.apiUser = new UserApiTestClient(mockMvc);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        String name = "Juan";
        String lastname = "Perez";
        String password = "goodpassword";
        String email = name + "_" + System.currentTimeMillis() + "@test.com";

        apiUser.register(name, lastname, email, password, password)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.email").value(email.trim().toLowerCase()))
                .andExpect(jsonPath("$.enabled").value(true));

    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    void shouldReturnBadRequestWhenUserFieldsAreInvalid(
            String name,
            String lastname,
            String email,
            String password,
            String confirmPassword,
            String expectedField) throws Exception {

        apiUser.register(name, lastname, email, password, confirmPassword)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField)).exists())
                .andExpect(
                        jsonPath("$.errors.%s".formatted(expectedField))
                                .value(org.hamcrest.Matchers.notNullValue()));

    }

    @Test
    void shouldReturnBadRequestWhenEmailIsAlreadyUsed() throws Exception {
        String name = "Juan";
        String lastname = "Perez";
        String pass = "goodpassword";
        String email = "DuplicateEmail" + "_" + System.currentTimeMillis() + "@test.com";

        apiUser.register(name, lastname, email, pass, pass)
                .andExpect(status().isOk());
        apiUser.register(name, lastname, email, pass, pass)
                .andExpect(status().isConflict());

    }

    @Test
    void shouldBeAbleToLoginImmediatelyAfterRegistration() throws Exception {
        String password = "goodpassword";
        String email = "auth_test_" + System.currentTimeMillis() + "@test.com";

        apiUser.register("Juan", "Perez", email, password, password)
                .andExpect(status().isOk());

        AuthApiTestClient authApi = new AuthApiTestClient(mockMvc);
        authApi.login(email, password)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    static Stream<Arguments> invalidUsers() {
        return Stream.of(
                // name vacío
                Arguments.of(
                        "", "Perez", "test@test.com", "goodPassword", "goodPassword", "name"),
                // lastname vacío
                Arguments.of(
                        "Juan", "", "test@test.com", "goodPassword", "goodPassword",
                        "lastname"),
                // email inválido
                Arguments.of(
                        "Juan", "Perez", "invalid-email", "goodPassword", "goodPassword",
                        "email"),
                // email vació
                Arguments.of(
                        "Juan", "Perez", "", "goodPassword", "goodPassword", "email"),
                // password corta
                Arguments.of(
                        "Juan", "Perez", "test@test.com", "wrong", "wrong", "password"),
                // passwords no coinciden
                Arguments.of(
                        "Juan", "Perez", "test@test.com", "goodPassword", "wrongPassword",
                        "confirmPassword"),
                // campos nulos
                Arguments.of(
                        null, null, null, null, null, "password"));
    }

}
