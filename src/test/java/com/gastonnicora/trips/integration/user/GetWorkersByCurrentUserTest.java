/* package com.gastonnicora.trips.integration.user;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.gastonnicora.trips.helpers.CompanyApiTestClient;
import com.gastonnicora.trips.helpers.UserApiTestClient;
import com.gastonnicora.trips.helpers.UserTestFactory;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GetWorkersByCurrentUserTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private UserApiTestClient userApi;
    private CompanyApiTestClient companyApi;
    private String email;
    private String password = "password";

    @BeforeEach
    void setup() throws Exception {
        email = UserTestFactory.registerUser(mockMvc, "Role_User", password).getEmail();
        token = UserTestFactory.login(mockMvc, email, password).getToken();
        this.userApi = new UserApiTestClient(mockMvc).withToken(token);

    }
}
 */