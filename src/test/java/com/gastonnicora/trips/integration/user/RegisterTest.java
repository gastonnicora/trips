package com.gastonnicora.trips.integration.user;

import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.user.UserApiTestClient;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegisterTest {

    UserApiTestClient apiUser;
    @Autowired
    private MockMvc mockMvc;

    static Stream<Arguments> invalidUsers() {
        return Stream.of(
                // name vacío
                Arguments.of(
                        "", "Perez", "test@test.com", "goodPassword", "goodPassword", "name"),
                // lastname vacío
                Arguments.of(
                        "Juan", "", "test@test.com", "goodPassword", "goodPassword",
                        "lastname"),
                Arguments.of(
                        "Juan", "Perez", "invalid-email", "goodPassword", "goodPassword",
                        "email"),
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

    @BeforeEach
    void setup() {
        this.apiUser = new UserApiTestClient(mockMvc);
    }

    @Test
    void shouldLoginSuccessfullyImmediatelyAfterRegistration() throws Exception {
        String password = "goodpassword";
        String email = "auth_test_" + System.currentTimeMillis() + "@test.com";

        apiUser.register("Juan", "Perez", email, password, password)
                .andExpect(status().isOk());

        AuthApiTestClient authApi = new AuthApiTestClient(mockMvc);
        authApi.login(email, password)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
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
                                .value(notNullValue()));

    }

}
