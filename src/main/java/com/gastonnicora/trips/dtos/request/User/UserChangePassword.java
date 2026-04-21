package com.gastonnicora.trips.dtos.request.User;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "DTO de usuario para put password")
public class UserChangePassword {

    private final int minLengthPass = 8;

    @Schema(description = "Contraseña actual", example = "12345678")
    @NotBlank(message = "La contraseña actual no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres", max = 255)
    private String passwordOld;

    @Schema(description = "Nueva contraseña", example = "12345678")
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres", max = 255)
    private String password;

    @Schema(description = "Repetición de la nueva contraseña", example = "12345678")
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres", max = 255)
    private String confirmPassword;

    public UserChangePassword(String passwordOld, String password, String confirmPassword) {
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
