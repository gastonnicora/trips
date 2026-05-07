package com.gastonnicora.trips.dtos.request.User;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

/**
 * DTO para cambiar la contraseña de un usuario.
 * <p>
 * Contiene la contraseña actual, la nueva contraseña y la confirmación de la
 * nueva contraseña.
 * Aplica validaciones para asegurar que los campos no estén vacíos, tengan una
 * longitud mínima
 * y máxima, y que la nueva contraseña coincida con la confirmación.
 * </p>
 * <p>
 * La validación {@link FieldsMatch} asegura que {@code password} y
 * {@code confirmPassword} sean iguales.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "DTO de usuario para cambiar la contraseña")
@NoArgsConstructor
public class UserChangePassword {

    private final int minLengthPass = 8;

    /**
     * Contraseña actual del usuario.
     */
    @Schema(description = "Contraseña actual", example = "12345678")
    @NotBlank(message = "La contraseña actual no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String passwordOld;

    /**
     * Nueva contraseña del usuario.
     */
    @Schema(description = "Nueva contraseña", example = "12345678")
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String password;

    /**
     * Confirmación de la nueva contraseña.
     */
    @Schema(description = "Repetición de la nueva contraseña", example = "12345678")
    @NotBlank(message = "La nueva contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, max = 255, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres")
    private String confirmPassword;

    /**
     * Constructor completo.
     *
     * @param passwordOld     Contraseña actual
     * @param password        Nueva contraseña
     * @param confirmPassword Confirmación de la nueva contraseña
     */
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