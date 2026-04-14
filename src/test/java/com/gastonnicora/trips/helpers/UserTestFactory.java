package com.gastonnicora.trips.helpers;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;

import tools.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTestFactory {

    // registra un usuario y devuelve un nuevo usuario
    public static UserDTOs registerUser(MockMvc mockMvc, String name, String pass) throws Exception {
        String email = name + "_" + System.currentTimeMillis() + "@test.com";

        MvcResult result = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "name": "%s",
                                "lastname": "Perez",
                                "email": "%s",
                                "password": "%s",
                                "confirmPassword": "%s"
                            }
                        """.formatted(name, email, pass, pass)))
                .andExpect(status().isOk())
                .andReturn(); // 👈 clave

        String responseJson = result.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseJson, UserDTOs.class);
    }

    // hace login de un usuario y devuelve el token
    public static String loginAndGetToken(MockMvc mockMvc, String email, String pass) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {"email":"%s","password":"%s"}
                        """.formatted(email, pass)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = response.split(":")[1].replaceAll("[\"}]", "").trim();
        return token;
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
                                        "passswordOld":%s,
                                        "password": "%s",
                                        "confirmPassword": "%s"
                                    }
                                """.formatted(passwordOld,password,confirmPassword);
        }

}