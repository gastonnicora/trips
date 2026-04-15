package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.helpers.UserApiTestClient;

import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
                                .andExpect(jsonPath("$.email").value(email))
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

                apiUser.register(name, lastname, email, password,confirmPassword)
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
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").exists()) //TODO modificar excepcion y mensaje para que muestre donde esta el error
                                .andExpect(jsonPath("$.errors.email").exists())
                                .andExpect(
                                                jsonPath("$.errors.email")
                                                                .value(org.hamcrest.Matchers.notNullValue()));

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

                                // email con espacio
                                Arguments.of(
                                                "Juan", "Perez", "test@test.com ", "goodPassword", "goodPassword",
                                                "email"),//TODO si hago trim eliminar
                                // password corta
                                Arguments.of(
                                                "Juan", "Perez", "test@test.com", "wrong", "wrong", "password"),

                                // passwords no coinciden
                                Arguments.of(
                                                "Juan", "Perez", "test@test.com", "goodPassword", "wrongPassword",
                                                "confirmPassword"),
                                        // password corta
                                Arguments.of(
                                                null,null,null,null,null, "password"));
        }

}
