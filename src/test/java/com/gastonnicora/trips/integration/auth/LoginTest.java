package com.gastonnicora.trips.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

        @Autowired
        private MockMvc mockMvc;

        UserDTOs user;
        final String  name= "user";
        final String pass= "goodPassword"; 

        @BeforeEach
        void setup() throws Exception {
                user = UserTestFactory.registerUser(mockMvc, name, pass);
        }

        // test login correcto
        @Test
        void shouldRegisterAndLogin() throws Exception {

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(), pass)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists());
        }

        // Login incorrecto por email
        @Test
        void testLoginFailEmail() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%saaffa","password":"%s"}
                                                """.formatted(user.getEmail(),pass)))
                                .andExpect(status().isUnauthorized());
        }

        // Login incorrecto por contraseña
        @Test
        void testLoginFailPass() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"wrongpass"}
                                                """.formatted(user.getEmail())))
                                .andExpect(status().isUnauthorized());
        }

        // login falla por pass demasiado larga
        @Test
        void testLongLengthInput() throws Exception {
                String pass = """
                                Lorem ipsum dolor sit amet, consectetuer adipiscing elit.
                                 Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus
                                  et magnis dis parturient montes, nascetur ridiculus mus.
                                   Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.
                                   Nulla consequat massa quis enim. Donec.

                                               """;
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(), pass.replace("\n", "").trim())))
                                .andExpect(status().isBadRequest());
        }

        // Login falla por email con espacio
        @Test
        void testEmailWithSpace() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s ","password":"%s"}
                                                """.formatted(user.getEmail(),pass)))
                                .andExpect(status().isBadRequest());
        }

        //Comprueba que se guarde correctamente la cookie de refreshToken
        @Test
        void testCookieRefreshToken() throws Exception {

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(),pass)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.refreshToken").value((Object) null));

        }

        // Comprueba que se pase el refreshtoken a android
        @Test
        void testRefreshTokenAndorid() throws Exception {
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("User-Agent", "okhttp/4.9.0 (Android)")
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(), pass)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().doesNotExist("refreshToken"))
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.refreshToken").exists())
                                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
        }
}
