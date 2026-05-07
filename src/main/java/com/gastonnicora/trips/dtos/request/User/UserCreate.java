package com.gastonnicora.trips.dtos.request.User;

import com.gastonnicora.trips.validations.FieldsMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

/**
 * DTO para crear un nuevo usuario.
 * <p>
 * Hereda de {@link UserBasic} e incluye la contraseña.
 * </p>
 * <ul>
 * <li>{@code name}: Nombre del usuario</li>
 * <li>{@code lastname}: Apellido del usuario</li>
 * <li>{@code email}: Correo electrónico del usuario</li>
 * <li>{@code password}: Contraseña del usuario</li>
 * <li>{@code confirmPassword}: Confirmación de la contraseña</li>
 * </ul>
 * 
 * <p>
 * Se utiliza en los endpoints de registro de usuarios (POST).
 * </p>
 * 
 * Ejemplo de JSON:
 * 
 * <pre>
 * {
 *   "name": "Gastón",
 *   "lastname": "Nicora",
 *   "email": "gaston@example.com",
 *   "password": "123456",
 *   "confirmPassword": "123456"
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "Las contraseñas deben coincidir")
@Schema(description = "DTO de usuario para creación (POST)")
@NoArgsConstructor
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