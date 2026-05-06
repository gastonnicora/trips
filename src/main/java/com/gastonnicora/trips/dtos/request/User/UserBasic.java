package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO básico de usuario.
 * <p>
 * Clase abstracta que contiene la información mínima de un usuario que se
 * comparte entre otros DTOs.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code name}: Nombre del usuario.</li>
 * <li>{@code lastname}: Apellido del usuario.</li>
 * <li>{@code email}: Correo electrónico del usuario.</li>
 * </ul>
 * 
 * <p>
 * Se utiliza como clase base para DTOs de creación y actualización de usuarios.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
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