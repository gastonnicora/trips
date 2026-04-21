package com.gastonnicora.trips.helpers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.dtos.response.auth.LoginResponse;

import tools.jackson.databind.ObjectMapper;

public class UserTestFactory {

    // registra un usuario y devuelve un nuevo usuario
    public static UserDTOs registerUser(MockMvc mockMvc, String name, String pass) throws Exception {
        String email = name + "_" + System.currentTimeMillis() + "@test.com";
        UserApiTestClient userApi = new UserApiTestClient(mockMvc);
        MvcResult result = userApi.register(name, "Perez", email, pass, pass)
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseJson, UserDTOs.class);
    }

    // hace login de un usuario y devuelve el token
    public static LoginResponse login(MockMvc mockMvc, String email, String pass) throws Exception {
        AuthApiTestClient authApi = new AuthApiTestClient(mockMvc);
        MvcResult response = authApi.login(email, pass)
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", org.hamcrest.Matchers.notNullValue()))
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        LoginResponse loginResponse = mapper.readValue(responseJson, LoginResponse.class);

        String refreshCookie = response.getResponse().getCookie("refreshToken").getValue();
        loginResponse.setRefreshToken(refreshCookie);
        return loginResponse;
    }

    public static LoginResponse loginWithAndroid(MockMvc mockMvc, String email, String pass) throws Exception {
        AuthApiTestClient authApi = new AuthApiTestClient(mockMvc);
        MvcResult response = authApi.loginWithUserAgent(email, pass, "okhttp/4.9.0 (Android)")
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseJson, LoginResponse.class);
    }

    public static String userJson(String name, String lastname, String email, String password, String confirmPassword) {
        return """
                    {
                        "name": "%s",
                        "lastname": "%s",
                        "email": "%s",
                        "password": "%s",
                        "confirmPassword": "%s"
                    }
                """.formatted(name, lastname, email, password, confirmPassword);
    }

    public static String userJsonPutPass(String passwordOld, String password, String confirmPassword) {
        return """
                    {
                        "passwordOld":"%s",
                        "password": "%s",
                        "confirmPassword": "%s"
                    }
                """.formatted(passwordOld, password, confirmPassword);
    }

}