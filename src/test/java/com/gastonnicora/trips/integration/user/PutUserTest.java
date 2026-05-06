package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PutUserTest {

        @Autowired
        private MockMvc mockMvc;

        UserDTO user;
        String token;
        UserApiTestClient apiUser;

        @BeforeEach
        void setup() throws Exception {

                String name = "Juan";
                String pass = "goodPassword";

                user = UserTestFactory.registerUser(mockMvc, name, pass);
                token = UserTestFactory.login(mockMvc, user.getEmail(), pass).getToken();
                this.apiUser = new UserApiTestClient(mockMvc).withToken(token);
        }

        @Test
        void shouldUpdateUserSuccessfully() throws Exception {
                final String newName = "Marta";
                final String newLastname = "Sierra";

                apiUser.update(newName, newLastname, user.getEmail())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value(user.getEmail()))
                                .andExpect(jsonPath("$.name").value(newName))
                                .andExpect(jsonPath("$.lastname").value(newLastname))
                                .andExpect(jsonPath("$.uuid").value(user.getUuid().toString()))
                                .andExpect(jsonPath("$.enabled").value(true));
        }

        @Test
        void shouldUpdateUserEmailSuccessfully() throws Exception {
                final String newName = "Marta";
                final String newLastname = "Sierra";
                final String newEmail = "martaSierra@mail.com";
                apiUser.update(newName, newLastname, newEmail)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value(newEmail.trim().toLowerCase()))
                                .andExpect(jsonPath("$.name").value(newName))
                                .andExpect(jsonPath("$.lastname").value(newLastname));
        }

        @Test
        void shouldReturnUnauthorizedWhenChangeEmail() throws Exception {
                final String newName = "Marta";
                final String newLastname = "Sierra";
                final String newEmail = "martaSierra@mail.com";
                apiUser.update(newName, newLastname, newEmail)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value(newEmail.trim().toLowerCase()))
                                .andExpect(jsonPath("$.name").value(newName))
                                .andExpect(jsonPath("$.lastname").value(newLastname));
                apiUser.update(newName, newLastname, newEmail)
                                .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @MethodSource("invalidUsers")
        void shouldReturnBadRequestWhenUserFieldsAreInvalid(
                        String name,
                        String lastname,
                        String email,
                        String expectedField) throws Exception {
                apiUser.update(name, lastname, email)
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").exists())
                                .andExpect(
                                                jsonPath("$.errors.%s".formatted(expectedField))
                                                                .value(org.hamcrest.Matchers.notNullValue()));

        }

        @Test
        void shouldReturnBadRequestWhenEmailIsAlreadyInUse() throws Exception {
                UserDTO otherUser = UserTestFactory.registerUser(mockMvc, "Maria", "goodPassword");
                apiUser.update(user.getName(), user.getLastname(), otherUser.getEmail())
                                .andExpect(status().isConflict());
        }

        @Test
        void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
                apiUser.withToken("");
                apiUser.update(user.getName(), user.getLastname(), user.getEmail())
                                .andExpect(status().isUnauthorized());
        }

        static Stream<Arguments> invalidUsers() {
                return Stream.of(
                                // name vacío
                                Arguments.of(
                                                "", "Perez", "test@test.com", "name"),

                                // lastname vacío
                                Arguments.of(
                                                "Juan", "", "test@test.com",
                                                "lastname"),

                                // email inválido
                                Arguments.of(
                                                "Juan", "Perez", "invalid-email",
                                                "email"),
                                // email vació
                                Arguments.of(
                                                "Juan", "Perez", "", "email"),

                                // campos en null
                                Arguments.of(
                                                null, null, null,
                                                "email"));
        }

}
