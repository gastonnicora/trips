package com.gastonnicora.trips.integration.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetMeTest {
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
    void shouldReturnUserSuccessfully() throws Exception {
        userApi.getMe()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void shouldReturnForbiddenWhenTokenIsMissing() throws Exception {
        userApi.withToken("");
        userApi.getMe()
                .andExpect(status().isForbidden());
    }   
    
}
