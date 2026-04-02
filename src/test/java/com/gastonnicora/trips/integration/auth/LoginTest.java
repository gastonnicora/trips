package com.gastonnicora.trips.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.helpers.AuxUser;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

        @Autowired
        private MockMvc mockMvc;


        //test login correcto
        @Test
        void shouldRegisterAndLogin() throws Exception {

                AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists());
        }

        //Login incorrecto por email
        @Test
        void TestLoginFailEmail() throws Exception {
                AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%saaffa","password":"%s"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isUnauthorized());
        }

        //Login incorrecto por contraseña
        @Test
        void TestLoginFailPass() throws Exception {
                AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"12"}
                                                """.formatted(user.getEmail())))
                                .andExpect(status().isUnauthorized());
        }

        //login falla por pass demasiado larga
        @Test
        void TestLongLengthInput() throws Exception {
                AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
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
        
        //Login falla por email con espacio 
        @Test
        void TestEmailWithSpace() throws Exception{
                AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s ","password":"%s"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isBadRequest());
        }

}
