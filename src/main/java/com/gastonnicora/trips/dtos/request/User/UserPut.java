package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de usuario para put")
public class UserPut extends UserBasic {

    public UserPut(String name, String lastname, String email) {
        super(name, lastname, email);
    }

}
