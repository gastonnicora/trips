package com.gastonnicora.trips.integration.user;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.user.UserApiTestClient;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UpdatePasswordTest {

    private final String name = "Juan";
    private final String password = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    private UserDTO user;
    private String token;
    private UserApiTestClient userApi;

    static Stream<Arguments> invalidFields() {
        return Stream.of(
                Arguments.of("", "newPassword", "newPassword", "passwordOld"),
                // Nueva contraseña vacía
                Arguments.of("goodPassword", "", "newPassword", "password"),
                // Nueva contraseña corta
                Arguments.of("goodPassword", "short", "short", "password"),
                // Repetición de contraseña vacía
                Arguments.of("goodPassword", "newPassword", "", "confirmPassword"),
                // Repetición de contraseña incorrecta
                Arguments.of("goodPassword", "newPassword", "wrongPassword", "confirmPassword"),
                // Campos en null
                Arguments.of(null, null, null, "passwordOld"),
                Arguments.of(null, null, null, "password"),
                Arguments.of(null, null, null, "confirmPassword"));
    }

    @BeforeEach
    void setup() throws Exception {

        user = UserTestFactory.registerUser(mockMvc, name, password);
        token = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
    }

    @Test
    void shouldLoginSuccessfullyWithNewPasswordAfterChange() throws Exception {
        String newPassword = "newPassword123";
        userApi.updatePassword(password, newPassword, newPassword)
                .andExpect(status().isOk());

        // Intentar login con la nueva contraseña
        AuthApiTestClient authApi = new AuthApiTestClient(mockMvc);
        authApi.login(user.getEmail(), newPassword)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @ParameterizedTest
    @MethodSource("invalidFields")
    void shouldReturnBadRequestWhenFieldsAreInvalid(String passwordOld,
                                                    String password,
                                                    String confirmPassword,
                                                    String expectedField) throws Exception {
        userApi.updatePassword(passwordOld, password, confirmPassword)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField)).exists())
                .andExpect(jsonPath("$.errors.%s".formatted(expectedField))
                        .value(notNullValue()));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsWrong() throws Exception {
        String newPassword = "newPassword";
        userApi.updatePassword("wrongPassword", newPassword, newPassword)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors.passwordOld").exists())
                .andExpect(jsonPath("$.errors.passwordOld")
                        .value(notNullValue()));
    }

    @Test
    void shouldReturnUnauthorizedWhenChangePassword() throws Exception {
        String newPassword = "newPassword";
        userApi.updatePassword(password, newPassword, newPassword)
                .andExpect(status().isOk());
        userApi.updatePassword(newPassword, password, password)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        userApi.withToken("");
        userApi.updatePassword("newPassword", "newPassword", "newPassword")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdatePasswordSuccessfully() throws Exception {
        String newPassword = "newPassword";
        userApi.updatePassword(password, newPassword, newPassword)
                .andExpect(status().isOk());
    }

}
