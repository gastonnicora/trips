package com.gastonnicora.trips.dtos.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;

//TODO Agregar mensajes si falta y Size

@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
public class UserCreate extends UserBasic{

    private final int minLengthPass = 4;
    

    @Schema(description = "Contraseña con mínimo "+ minLengthPass+" caracteres.", example = "12345678")
    @NotBlank(message = "Debe introducir una contraseña")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass + " caracteres")
    private String password;

    @Schema(description = "Repetir contraseña", example = "12345678")
    @NotBlank(message = "Debe introducir una contraseña")
    private String confirmPassword;

    public UserCreate(String name, String lastname, String email, String password, String confirmPass) {
        super(name, lastname, email);
        this.password = password;
        this.confirmPassword = confirmPass;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPass) {
        this.confirmPassword = confirmPass;
    }
    

}
