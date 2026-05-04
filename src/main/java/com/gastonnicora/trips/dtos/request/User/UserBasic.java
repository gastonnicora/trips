package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO básico para solicitudes de usuario.
 * <p>
 * Esta clase abstracta se utiliza como base para los DTOs de request de
 * usuario.
 * Contiene los datos principales del usuario: nombre, apellido y email.
 * Incluye validaciones para asegurar que los campos no queden vacíos y tengan
 * el formato correcto.
 * </p>
 * <p>
 * El email se normaliza automáticamente a minúsculas y sin espacios en los
 * extremos.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Schema(description = "DTO de usuario para Request")
public abstract class UserBasic {

    /**
     * Nombre del usuario.
     */
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    /**
     * Apellido del usuario.
     */
    @Schema(description = "Apellido del usuario", example = "Perez")
    @NotBlank(message = "El apellido no puede quedar en blanco")
    @Size(max = 255, message = "El apellido no puede tener mas de 255 caracteres")
    private String lastname;

    /**
     * Email del usuario.
     */
    @Schema(description = "Email del usuario", example = "juanperez@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Email(message = "El email no es valido")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;

    /**
     * Constructor completo.
     * 
     * @param name     Nombre del usuario
     * @param lastname Apellido del usuario
     * @param email    Email del usuario
     */
    public UserBasic(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

}