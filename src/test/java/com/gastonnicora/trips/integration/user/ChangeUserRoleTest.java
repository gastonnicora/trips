package com.gastonnicora.trips.integration.user;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.test.web.servlet.MvcResult;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChangeUserRoleTest {

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
        void shouldReturnOk_whenAddRoleAdmin() throws Exception {
                userApi.changeRole(user.getUuid().toString(), Set.of(Role.ADMIN))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role").isArray())
                                .andExpect(jsonPath("$.role", hasSize(2)))
                                .andExpect(jsonPath("$.role", hasItem(Role.ADMIN.name())))// se puede sustituir por
                                                                                          // containsInAnyOrder("ADMIN",
                                                                                          // "OTRO_ROLE")
                                .andExpect(jsonPath("$.role", hasItem(Role.USER.name()))); // User siempre esta
        }

        @Test
        void shouldReturnOk_whenAddMultipleRoles() throws Exception {
                Set<Role> roles = Set.of(Role.USER, Role.HR_MANAGER, Role.ADMIN);
                userApi.changeRole(user.getUuid().toString(), roles)
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role").isArray())
                                .andExpect(jsonPath("$.role", hasSize(3)))
                                .andExpect(jsonPath("$.role", containsInAnyOrder("ADMIN", "HR_MANAGER", "USER")));
        }

        @Test
        void shouldReturnOk_whenRemoveRoleAdmin() throws Exception {
                Set<Role> roles = Set.of(Role.USER, Role.HR_MANAGER, Role.ADMIN);
                userApi.changeRole(user.getUuid().toString(), roles)
                                .andExpect(status().isOk());
                userApi.changeRole(user.getUuid().toString(), Set.of(Role.USER))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.role", hasSize(1)))
                                .andExpect(jsonPath("$.role", hasItem(Role.USER.name())));
        }

        @Test
        void shouldReturnBadRequest_whenChangeRoleForSuperAdmin() throws Exception {
                MvcResult result = userApi.getMe()
                                .andExpect(status().isOk())
                                .andReturn();

                String superAdminUuid = JsonPath.read(
                                result.getResponse().getContentAsString(),
                                "$.uuid");
                userApi.changeRole(superAdminUuid, Set.of(Role.ADMIN))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors").exists())
                                .andExpect(jsonPath("$.errors.role").exists());
        }

        @Test
        void shouldReturnForbidden_whenUserNotHavePerMission() throws Exception {
                String tokenUser = UserTestFactory.login(mockMvc, user.getEmail(), userPassword).getToken();
                userApi.withToken(tokenUser);
                userApi.changeRole(user.getUuid().toString(), Set.of(Role.ADMIN))
                                .andExpect(status().isForbidden());
        }

        @Test
        void shouldReturnUnauthorized_whenTokenIsMissing() throws Exception {
                userApi.withToken(null);
                userApi.changeRole(user.getUuid().toString(), Set.of(Role.ADMIN))
                                .andExpect(status().isUnauthorized());
        }
}
