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
class AuthIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void shouldRegisterAndLogin() throws Exception {
                
                AuxUser user = UserTestFactory.registerUser(mockMvc,"user", "1234");
                // login
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%s","password":"%s"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists());
        }


        @Test
        void TestLoginFailCredentials() throws Exception {
                AuxUser user = UserTestFactory.registerUser(mockMvc,"user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%saaffa","password":"%s"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void TestLoginFailValid() throws Exception {
                AuxUser user = UserTestFactory.registerUser(mockMvc,"user", "1234");
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                    {"email":"%saaffa","password":"12"}
                                                """.formatted(user.getEmail(), user.getPass())))
                                .andExpect(status().isUnauthorized());
        }


}
