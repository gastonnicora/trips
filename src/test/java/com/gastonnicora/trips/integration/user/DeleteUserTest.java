package com.gastonnicora.trips.integration.user;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.helpers.AuthApiTestClient;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeleteUserTest {

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
    void shouldDeleteUserAndPreventSubsequentLogin() throws Exception {

        userApi.deleteCurrentUser()
                .andExpect(status().isOk());

        AuthApiTestClient newClient = new AuthApiTestClient(mockMvc);

        newClient.login(user.getEmail(), password)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        userApi.deleteCurrentUser()
                .andExpect(status().isOk());
    }

    @Test
    void shouldInvalidateTokenImmediatelyAfterDeletion() throws Exception {
        // Borrar usuario exitosamente
        userApi.deleteCurrentUser()
                .andExpect(status().isOk());

        // Intentar obtener mis datos con el token que acaba de ser borrado
        userApi.getMe()
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotAllowDeletingAlreadyDeletedUser() throws Exception {
        userApi.deleteCurrentUser()
                .andExpect(status().isOk());

        // El segundo intento debería fallar porque la sesión/usuario ya no existen
        userApi.deleteCurrentUser()
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        userApi.withToken("");
        userApi.deleteCurrentUser()
                .andExpect(status().isUnauthorized());
    }

}
