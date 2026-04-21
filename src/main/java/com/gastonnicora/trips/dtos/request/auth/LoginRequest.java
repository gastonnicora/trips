package com.gastonnicora.trips.dtos.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Debe introducir un email")
    @Email(message = "Debe introducir un email valido")
    @Size(max = 255, message = "El email no puede ser de mas de 255 caracteres")
    @Schema(description = "Su email", example = "juanperez@mail.com")
    private String email;

    @NotBlank(message = "Debe introducir una contraseña")
    @Size(max = 255, message = "La contraseña debe tener entre 0 y 255 caracteres")
    @Schema(description = "Contraseña", example = "12345678")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginRequest(
            String email, String password) {
        this.email = email.trim().toLowerCase();
        this.password = password;
    }

}
