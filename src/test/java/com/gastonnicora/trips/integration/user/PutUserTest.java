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

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PutUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPutUser() throws Exception {
        String name = "Juan";
        String pass = "goodPassword";

        UserDTOs user = UserTestFactory.registerUser(mockMvc, name, pass);
        String token = UserTestFactory.loginAndGetToken(mockMvc, user.getEmail(), pass);
        final String newName= "Marta";
        final String newLastname="Sierra";
        final String newEmail="martaSierra@mail.com";
        mockMvc.perform(put("/api/user")
                .header("Authorization", "Bearer " + token)
                .content(UserTestFactory.userJson(newName,newLastname,newEmail,null,null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.lastname").value(newLastname));
    }

}
