package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DeleteUserTest {

    @Autowired
    private MockMvc mockMvc;

    private UserDTOs user;
    private String token;
    private UserApiTestClient userApi;
    private String name = "Juan";
    private String password = "goodPassword";

    @BeforeEach
    void setup() throws Exception {

        user = UserTestFactory.registerUser(mockMvc, name, password);
        token = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        userApi.deleteCurrentUser()
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        userApi.withToken("");
        userApi.deleteCurrentUser()
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDeleteUserSuccessfully_andUserShouldNotBeAbleToLogin() throws Exception {

        userApi.deleteCurrentUser()
                .andExpect(status().isOk());

        AuthApiTestClient newClient = new AuthApiTestClient(mockMvc);

        newClient.login(user.getEmail(), password)
                .andExpect(status().isUnauthorized());
    }

}
