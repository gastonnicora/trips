package com.gastonnicora.trips.integration.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.helpers.AuxUser;
import com.gastonnicora.trips.helpers.UserTestFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;



   
    @Test
    void shouldRegisterUser() throws Exception {
        String email= "user@hotmail.com";
        String pass = "1234";
        //
        // register
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                            "name": "Juan",
                            "lastname": "Perez",
                            "email":"%s",
                            "password": "%s",
                            "confirmPassword": "%s"

                            }
                        """.formatted(email, pass, pass)))
                .andExpect(status().isOk());
    }


    //Acceder endpoint protegido
    @Test
    void shouldAccessProtectedEndpoint() throws Exception {
        
        AuxUser user = UserTestFactory.registerUser(mockMvc, "user", "1234");
        String token = UserTestFactory.loginAndGetToken(mockMvc, user.getEmail(), user.getPass());
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    // Sin token → forbidden
    @Test
    void shouldFailWithoutToken() throws Exception {

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden());
    }

    //Token inválido
    @Test
    void shouldFailWithInvalidToken() throws Exception {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIn0.invalidsignature";
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer "+ invalidToken))
                .andExpect(status().isForbidden());
    }
}