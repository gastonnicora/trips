package com.gastonnicora.trips.helpers;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTestFactory {

    //registra un usuario y devuelve el email y la contraseña
    public static AuxUser registerUser(MockMvc mockMvc, String name, String pass) throws Exception {
        String email = name + "_" + System.currentTimeMillis() + "@test.com";

        mockMvc.perform(post("/api/user")
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
                .andExpect(status().isOk());


        return new AuxUser(email, pass);
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

}