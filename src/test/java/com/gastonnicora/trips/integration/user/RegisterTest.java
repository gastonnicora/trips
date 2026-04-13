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

import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

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
                .content(userJson(name, lastname, email, pass, pass)))
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
                .content(userJson(name, lastname, email, password, confirmPassword)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(
                        jsonPath("$.errors.%s".formatted(expectedField)).value(org.hamcrest.Matchers.notNullValue()));
                        
    }

    @Test
    void testRegisterFailByEmailUsed() throws Exception{
         String name = "Juan";
        String lastname = "Perez";
        String pass = "goodpassword";
        String email = "DuplicateEmail" + "_" + System.currentTimeMillis() + "@test.com";

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson(name, lastname, email, pass, pass)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.enabled").value(true));
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson(name, lastname, email, pass, pass)))
                .andExpect(status().isBadRequest());
        
    }
    private String userJson(String name, String lastname, String email, String pass, String confirm) {
    return """
        {
            "name": "%s",
            "lastname": "%s",
            "email": "%s",
            "password": "%s",
            "confirmPassword": "%s"
        }
    """.formatted(name, lastname, email, pass, confirm);
}

    static Stream<org.junit.jupiter.params.provider.Arguments> invalidUsers() {
        return Stream.of(
                // name vacío
                org.junit.jupiter.params.provider.Arguments.of(
                        "", "Perez", "test@test.com", "12345678", "12345678", "name"),

                // lastname vacío
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "", "test@test.com", "12345678", "12345678", "lastname"),

                // email inválido
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "invalid-email", "12345678", "12345678", "email"),

                // password corta
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "test@test.com", "123", "123", "password"),

                // passwords no coinciden (custom validation)
                org.junit.jupiter.params.provider.Arguments.of(
                        "Juan", "Perez", "test@test.com", "12345678", "99999999", "confirmPassword"));
    }

}
