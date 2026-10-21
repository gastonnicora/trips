package com.gastonnicora.trips.integration.user;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.user.UserApiTestClient;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GetMeTest {

    private final String name = "Juan";
    private final String password = "goodPassword";
    @Autowired
    private MockMvc mockMvc;
    private UserDTO user;
    private String token;
    private UserApiTestClient userApi;

    @BeforeEach
    void setup() throws Exception {

        user = UserTestFactory.registerUser(mockMvc, name, password);
        token = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        userApi.withToken("");
        userApi.getMe()
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUserSuccessfully() throws Exception {
        userApi.getMe()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.uuid").exists())
                .andExpect(jsonPath("$.enabled").value(true));
    }

}
