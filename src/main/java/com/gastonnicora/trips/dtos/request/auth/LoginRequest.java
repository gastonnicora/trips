package com.gastonnicora.trips.dtos.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

/**
 * DTO utilizado para solicitar la autenticación de un usuario.
 *
 * <p>
 * Contiene las credenciales necesarias para iniciar sesión en la aplicación:
 * dirección de correo electrónico y contraseña.
 * </p>
 *
 * <p>
 * El correo electrónico se normaliza automáticamente al establecer su valor,
 * convirtiéndolo a minúsculas y eliminando los espacios al inicio y al final.
 * </p>
 *
 * <p>
 * Los campos son validados mediante Jakarta Bean Validation antes de procesar
 * la solicitud.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-11
 */
@Schema(description = "Credenciales necesarias para iniciar sesión")
@NoArgsConstructor
@Setter
@Getter
public class LoginRequest {

    /**
     * Dirección de correo electrónico utilizada como identificador del usuario.
     *
     * <p>
     * Debe tener un formato de correo electrónico válido, no puede estar vacía
     * y no puede superar los 255 caracteres.
     * </p>
     *
     * <p>
     * El valor se normaliza automáticamente a minúsculas y se eliminan los
     * espacios al inicio y al final.
     * </p>
     */
    @NotBlank(message = "Debe introducir un email")
    @Email(message = "Debe introducir un email valido")
    @Size(max = 255, message = "El email no puede ser de mas de 255 caracteres")
    @Schema(
            description = "Dirección de correo electrónico del usuario.",
            example = "juanperez@mail.com",
            maxLength = 255
    )
    private String email;

    /**
     * Contraseña utilizada para autenticar al usuario.
     *
     * <p>
     * No puede estar vacía y no puede superar los 255 caracteres.
     * </p>
     *
     * <p>
     * La contraseña no se incluye en las respuestas de la API.
     * </p>
     */
    @NotBlank(message = "Debe introducir una contraseña")
    @Size(max = 255, message = "La contraseña debe tener entre 0 y 255 caracteres")
    @Schema(
            description = "Contraseña del usuario.",
            example = "********",
            format = "password",
            maxLength = 255
    )
    private String password;


    /**
     * Crea una solicitud de inicio de sesión con las credenciales indicadas.
     *
     * <p>
     * El correo electrónico se normaliza a minúsculas y se eliminan los
     * espacios al inicio y al final.
     * </p>
     *
     * @param email    dirección de correo electrónico del usuario.
     * @param password contraseña del usuario.
     */
    public LoginRequest(String email, String password) {
        this.email = email.trim().toLowerCase();
        this.password = password;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * <p>
     * El valor se normaliza convirtiéndolo a minúsculas y eliminando los
     * espacios al inicio y al final.
     * </p>
     *
     * @param email dirección de correo electrónico del usuario.
     */
    public void setEmail(@NonNull String email) {
        this.email = email.trim().toLowerCase();
    }
}