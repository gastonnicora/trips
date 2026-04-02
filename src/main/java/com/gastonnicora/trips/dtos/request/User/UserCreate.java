package com.gastonnicora.trips.dtos.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;

@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
public class UserCreate extends UserBasic{

    private final int minLengthPass = 8;
    

    @Schema(description = "Contraseña con mínimo "+ minLengthPass+" caracteres.", example = "12345678")
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos "+ minLengthPass +" y máximo 255 caracteres",max = 255)
    private String password;

    @Schema(description = "Repetición de la contraseña", example = "12345678")
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos "+ minLengthPass +" y máximo 255 caracteres",max = 255)
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
