package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegisterTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testRegister() throws Exception {
                String name = "Juan";
                String lastname = "Perez";
                String pass = "goodpassword";
                String email = name + "_" + System.currentTimeMillis() + "@test.com";

                mockMvc.perform(post("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(UserTestFactory.userJson(name, lastname, email, pass, pass)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uuid").exists())
                                .andExpect(jsonPath("$.email").value(email))
                                .andExpect(jsonPath("$.enabled").value(true));

        }

        @ParameterizedTest
        @MethodSource("invalidUsers")
        void testRegisterValidationErrors(
                        String name,
                        String lastname,
                        String email,
                        String password,
                        String confirmPassword,
                        String expectedField) throws Exception {

                mockMvc.perform(post("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(UserTestFactory.userJson(name, lastname, email, password, confirmPassword)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").exists())
                                .andExpect(
                                                jsonPath("$.errors.%s".formatted(expectedField))
                                                                .value(org.hamcrest.Matchers.notNullValue()));

        }

        @Test
        void testRegisterFailByEmailUsed() throws Exception {
                String name = "Juan";
                String lastname = "Perez";
                String pass = "goodpassword";
                String email = "DuplicateEmail" + "_" + System.currentTimeMillis() + "@test.com";

                mockMvc.perform(post("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(UserTestFactory.userJson(name, lastname, email, pass, pass)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.uuid").exists())
                                .andExpect(jsonPath("$.email").value(email))
                                .andExpect(jsonPath("$.enabled").value(true));
                mockMvc.perform(post("/api/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(UserTestFactory.userJson(name, lastname, email, pass, pass)))
                                .andExpect(status().isBadRequest());

        }

       

        static Stream<org.junit.jupiter.params.provider.Arguments> invalidUsers() {
                return Stream.of(
                                // name vacío
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "", "Perez", "test@test.com", "goodPassword", "goodPassword", "name"),

                                // lastname vacío
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "", "test@test.com", "goodPassword", "goodPassword",
                                                "lastname"),

                                // email inválido
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "Perez", "invalid-email", "goodPassword", "goodPassword",
                                                "email"),
                                // email vació
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "Perez", "", "goodPassword", "goodPassword", "email"),

                                // email con espacio
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "Perez", "test@test.com ", "goodPassword", "goodPassword",
                                                "email"),
                                // password corta
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "Perez", "test@test.com", "wrong", "wrong", "password"),

                                // passwords no coinciden
                                org.junit.jupiter.params.provider.Arguments.of(
                                                "Juan", "Perez", "test@test.com", "goodPassword", "worngPassword",
                                                "confirmPassword"));
        }

}
