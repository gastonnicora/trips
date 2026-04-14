package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PutUserTest {

    @Autowired
    private MockMvc mockMvc;

    UserDTOs user;
    String token;

    @BeforeEach
    void setup() throws Exception {

        String name = "Juan";
        String pass = "goodPassword";

        user = UserTestFactory.registerUser(mockMvc, name, pass);
        token = UserTestFactory.login(mockMvc, user.getEmail(), pass).getToken();
    }

    @Test
    void testPutUser() throws Exception {
        final String newName = "Marta";
        final String newLastname = "Sierra";
        mockMvc.perform(put("/api/user")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "JUnit-Test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(newName, newLastname, user.getEmail(), null, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.lastname").value(newLastname));
    }

    // TODO corregir accion al cambiar el email
    @Test
    void testPutUserWithNewEmail() throws Exception {
        final String newName = "Marta";
        final String newLastname = "Sierra";
        final String newEmail = "martaSierra@mail.com";
        mockMvc.perform(put("/api/user")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(newName, newLastname, newEmail, null, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.lastname").value(newLastname));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    void testPutUserValidationErrors(
            String name,
            String lastname,
            String email,
            String expectedField) throws Exception {

        mockMvc.perform(put("/api/user")
                .header("Authorization", "Bearer " + token)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(name, lastname, email, null, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(
                        jsonPath("$.errors.%s".formatted(expectedField))
                                .value(org.hamcrest.Matchers.notNullValue()));

    }

    @Test
    void testPutUserFailByEmailUsed() throws Exception {
        UserDTOs otherUser = UserTestFactory.registerUser(mockMvc, "Maria", "goodPassword");
        mockMvc.perform(put("/api/user")
                .with(csrf())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        UserTestFactory.userJson(user.getName(), user.getLastname(), otherUser.getEmail(), null, null)))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errors.email")
                                .value(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void testPutUserFailWithoutToken() throws Exception {
        mockMvc.perform(put("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestFactory.userJson(user.getName(), user.getLastname(), user.getEmail(), null, null)))
                .andExpect(status().isForbidden());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> invalidUsers() {
        return Stream.of(
                // name vacío
                org.junit.jupiter.params.provider.Arguments.of(
                        "", "Perez", "test@test.com", "name"),

                // lastname vacío
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "", "test@test.com",
                        "lastname"),

                // email inválido
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "invalid-email",
                        "email"),
                // email vació
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "", "email"),

                // email con espacio
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "test@test.com ",
                        "email"));
    }

}
