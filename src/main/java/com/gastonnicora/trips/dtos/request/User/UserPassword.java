package com.gastonnicora.trips.dtos.request.User;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


//TODO Agregar mensajes si falta y Size
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
public class UserPassword {
    
    private final int minLengthPass = 4;


    @Schema(description = "Contraseña actual", example = "12345678")
    @NotBlank(message = "Debe introducir su contraseña")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass + " caracteres")    
    private String passwordOld;

    @Schema(description = "Nueva contraseña con mínimo "+ minLengthPass+" caracteres.", example = "12345678")
    @NotBlank(message = "Debe introducir una contraseña nueva")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass + " caracteres")
    private String password;

    @Schema(description = "Repita la nueva contraseña", example = "12345678")
    @NotBlank(message = "Debe repetir la nueva contraseña")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass + " caracteres")
    private String confirmPassword;

    public UserPassword(String passwordOld, String password, String confirmPassword) {
        this.passwordOld = passwordOld;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
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

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    

}
