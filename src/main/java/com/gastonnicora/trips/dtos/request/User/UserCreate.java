package com.gastonnicora.trips.dtos.request.User;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un nuevo usuario.
 * <p>
 * Hereda de {@link UserBasic} e incluye campos de contraseña con validación.
 * Aplica la validación {@link FieldsMatch} para asegurar que la contraseña y la
 * confirmación coincidan.
 * </p>
 * <p>
 * Se utiliza típicamente en el endpoint de creación de usuarios (POST).
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "DTO de usuario para creación (POST)")
public class UserCreate extends UserBasic {

    /**
     * Longitud mínima de la contraseña.
     */
    private final int minLengthPass = 8;

    /**
     * Contraseña del usuario.
     */
    @Schema(description = "Contraseña con mínimo " + minLengthPass + " caracteres.", example = "12345678")
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres", max = 255)
    private String password;

    /**
     * Confirmación de la contraseña.
     */
    @Schema(description = "Repetición de la contraseña", example = "12345678")
    @NotBlank(message = "La contraseña no puede quedar en blanco")
    @Size(min = minLengthPass, message = "La contraseña debe contener al menos " + minLengthPass
            + " y máximo 255 caracteres", max = 255)
    private String confirmPassword;

    /**
     * Constructor completo.
     * 
     * @param name        Nombre del usuario
     * @param lastname    Apellido del usuario
     * @param email       Email del usuario
     * @param password    Contraseña del usuario
     * @param confirmPass Confirmación de la contraseña
     */
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