package com.gastonnicora.trips.integration.auth;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;
import com.gastonnicora.trips.helpers.user.UserTestFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifica por qué canal recibe el refresh token cada tipo de cliente:
 * cookie para navegadores (incluidos los móviles) y cuerpo para apps nativas.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClientTypeTest {

    private static final String IOS_APP = "TripsApp/1.0 CFNetwork/1494.0.7 Darwin/23.4.0";
    private static final String CHROME_ANDROID = "Mozilla/5.0 (Linux; Android 13; Pixel 7) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
    private static final String SAFARI_IPHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";

    private final String pass = "goodPassword";

    @Autowired
    private MockMvc mockMvc;

    private String email;

    private ResultActions login(String userAgent) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", userAgent)
                .content("""
                        {"email":"%s","password":"%s"}
                        """.formatted(email, pass)));
    }

    @BeforeEach
    void setup() throws Exception {
        UserDTO user = UserTestFactory.registerUser(mockMvc, "user", pass);
        email = user.getEmail();
    }

    @Test
    void shouldAllowIosAppToRefreshWithTokenFromBody() throws Exception {
        MvcResult result = login(IOS_APP).andExpect(status().isOk()).andReturn();
        LoginResponse loginResponse = new ObjectMapper()
                .readValue(result.getResponse().getContentAsString(), LoginResponse.class);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("User-Agent", IOS_APP)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(loginResponse.getRefreshToken())))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void shouldReturnCookieAndNoRefreshTokenInBodyForChromeOnAndroid() throws Exception {
        login(CHROME_ANDROID)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }

    @Test
    void shouldReturnCookieAndNoRefreshTokenInBodyForSafariOnIphone() throws Exception {
        login(SAFARI_IPHONE)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }

    @Test
    void shouldReturnRefreshTokenInBodyAndNoCookieForIosApp() throws Exception {
        login(IOS_APP)
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }
}