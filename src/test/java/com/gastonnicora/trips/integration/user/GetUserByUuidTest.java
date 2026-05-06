package com.gastonnicora.trips.integration.user;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetUserByUuidTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private UserApiTestClient userApi;
    @Value("${superadmin.email}")
    private String adminEmail;
    @Value("${superadmin.password}")
    private String adminPassword;
    private final String userPassword = "goodPassword";
    private UserDTO user;

    @BeforeEach
    void setup() throws Exception {
        token = UserTestFactory.login(mockMvc, adminEmail, adminPassword).getToken();
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);
        this.user = UserTestFactory.registerUser(mockMvc, "User", userPassword);
    }

    @Test
    void shouldReturnOk_whenSuperAdminSeachAndUserExists() throws Exception {
        userApi.getUser(user.getUuid().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").isArray())
                .andExpect(jsonPath("$.role", hasSize(1)))
                .andExpect(jsonPath("$.role", hasItem(Role.USER.name())));
    }

    @Test
    void shouldReturnOk_whenAdminSeachAndUserExists() throws Exception {
        userApi.changeRole(user.getUuid().toString(), Set.of(Role.ADMIN))
                .andExpect(status().isOk());
        String tokenUser = UserTestFactory.login(mockMvc, user.getEmail(), userPassword).getToken();
        userApi.withToken(tokenUser);
        userApi.getUser(user.getUuid().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").isArray())
                .andExpect(jsonPath("$.role", hasSize(2)))
                .andExpect(jsonPath("$.role", hasItem(Role.USER.name())))
                .andExpect(jsonPath("$.role", hasItem(Role.ADMIN.name())));
    }

    @Test
    void shouldReturnForbidden_whenRegularUserSearchesOtherUser() throws Exception {
        String userToken = UserTestFactory.login(mockMvc, user.getEmail(), userPassword).getToken();
        userApi.withToken(userToken);
        userApi.getUser(user.getUuid().toString())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
        userApi.withToken(null);
        userApi.getUser(user.getUuid().toString())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        String nonExistentUuid = UUID.randomUUID().toString();
        userApi.getUser(nonExistentUuid)
                .andExpect(status().isNotFound());
    }
}
