package com.gastonnicora.trips.integration.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetUsersTest {
        @Autowired
        private MockMvc mockMvc;

        private String token;
        private UserApiTestClient userApi;
        @Value("${superadmin.email}")
        private String email;
        @Value("${superadmin.password}")
        private String password;

        @BeforeEach
        void setup() throws Exception {
                token = UserTestFactory.login(mockMvc, email, password).getToken();
                this.userApi = new UserApiTestClient(mockMvc).withToken(token);
        }

        @Test
        void shouldReturnOk_whenUserIsSuperAdmin() throws Exception {
                userApi.getUsers()
                                .andExpect(status().isOk());
        }

        @Test
        void shouldReturnOk_whenUserIsAdmin() throws Exception {
                UserDTOs user = UserTestFactory.registerUser(mockMvc, "Role_Admin", password);
                // cambio de rol a un usuario
                userApi.changeRole(user.getUuid().toString(), Set.of(Role.ADMIN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").isNotEmpty());;

                token = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
                userApi = new UserApiTestClient(mockMvc).withToken(token);

                userApi.getUsers()
                                .andExpect(status().isOk());
        }

        @Test
        void shouldReturnForbidden_whenUserIsRegularUser() throws Exception {
                UserDTOs user = UserTestFactory.registerUser(mockMvc, "Role_User", password);

                token = UserTestFactory.login(mockMvc, user.getEmail(), password).getToken();
                userApi = new UserApiTestClient(mockMvc).withToken(token);

                userApi.getUsers()
                                .andExpect(status().isForbidden());
        }

        @Test
        void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {

                token = null;
                userApi = new UserApiTestClient(mockMvc).withToken(token);

                userApi.getUsers()
                                .andExpect(status().isUnauthorized());
        }
}
