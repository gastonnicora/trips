package com.gastonnicora.trips.dtos.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el inicio de sesión del usuario.
 * <p>
 * Contiene los datos necesarios para autenticarse en el sistema: email y
 * contraseña.
 * Se valida que el email tenga formato correcto y que los campos no estén en
 * blanco.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
public class LoginRequest {

    /**
     * Email del usuario que se utilizará para iniciar sesión.
     * <p>
     * Se valida que no esté en blanco, que tenga formato de correo válido y que
     * no supere los 255 caracteres.
     * </p>
     */
    @NotBlank(message = "Debe introducir un email")
    @Email(message = "Debe introducir un email valido")
    @Size(max = 255, message = "El email no puede ser de mas de 255 caracteres")
    @Schema(description = "Su email", example = "juanperez@mail.com")
    private String email;

    /**
     * Contraseña del usuario para iniciar sesión.
     * <p>
     * Se valida que no esté en blanco y que no supere los 255 caracteres.
     * </p>
     */
    @NotBlank(message = "Debe introducir una contraseña")
    @Size(max = 255, message = "La contraseña debe tener entre 0 y 255 caracteres")
    @Schema(description = "Contraseña", example = "12345678")
    private String password;

    /**
     * Obtiene el email del usuario.
     * 
     * @return email en minúsculas y sin espacios al inicio o final
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario.
     * <p>
     * Se convierte a minúsculas y se elimina espacios al inicio y final.
     * </p>
     * 
     * @param email email del usuario
     */
    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * 
     * @param password contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Constructor completo de LoginRequest.
     * 
     * @param email    email del usuario (se normaliza a minúsculas y se quitan
     *                 espacios)
     * @param password contraseña del usuario
     */
    public LoginRequest(String email, String password) {
        this.email = email.trim().toLowerCase();
        this.password = password;
    }
}