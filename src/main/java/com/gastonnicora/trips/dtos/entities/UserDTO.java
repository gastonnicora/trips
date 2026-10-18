package com.gastonnicora.trips.dtos.entities;

import com.gastonnicora.trips.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO utilizado para representar la información de un usuario en las respuestas
 * de la API.
 *
 * <p>
 * Expone los datos necesarios para identificar y mostrar un usuario sin incluir
 * información sensible, como su contraseña.
 * </p>
 *
 * <p>
 * Los roles contenidos en este DTO representan los permisos globales asignados
 * al usuario dentro de la aplicación.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "Información pública de un usuario")
public class UserDTO {

    /**
     * Identificador único del usuario.
     */
    @Schema(
            description = "Identificador único del usuario.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Nombre del usuario.
     */
    @Schema(
            description = "Nombre del usuario.",
            example = "Juan"
    )
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    /**
     * Apellido del usuario.
     */
    @Schema(
            description = "Apellido del usuario.",
            example = "Perez"
    )
    @NotBlank(message = "El apellido no puede quedar en blanco")
    @Size(max = 255, message = "El apellido no puede tener mas de 255 caracteres")
    private String lastname;

    /**
     * Dirección de correo electrónico del usuario.
     */
    @Schema(
            description = "Dirección de correo electrónico del usuario.",
            example = "juanperez@mail.com"
    )
    @NotBlank(message = "El email no puede quedar en blanco")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;

    /**
     * Conjunto de roles globales asignados al usuario.
     */
    @Schema(
            description = "Roles globales asignados al usuario.",
            example = "[\"USER\"]"
    )
    private Set<Role> role;

    /**
     * Indica si la cuenta del usuario está habilitada.
     *
     * <p>
     * Un usuario deshabilitado no puede utilizar la cuenta normalmente.
     * </p>
     */
    @Schema(
            description = "Indica si la cuenta del usuario está habilitada.",
            example = "true"
    )
    private boolean enabled;

    /**
     * Fecha y hora en la que se creó el usuario.
     */
    @Schema(
            description = "Fecha y hora de creación del usuario.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del usuario.
     */
    @Schema(
            description = "Fecha y hora de la última actualización del usuario.",
            example = "2026-01-01T00:00:00"
    )
    private LocalDateTime updatedAt;

    /**
     * Crea una instancia completa del DTO de usuario.
     *
     * @param uuid      identificador único del usuario.
     * @param name      nombre del usuario.
     * @param lastname  apellido del usuario.
     * @param email     dirección de correo electrónico del usuario.
     * @param role      conjunto de roles globales asignados al usuario.
     * @param enabled   indica si la cuenta está habilitada.
     * @param createdAt fecha y hora de creación.
     * @param updatedAt fecha y hora de la última actualización.
     */
    public UserDTO(
            UUID uuid,
            String name,
            String lastname,
            String email,
            Set<Role> role,
            boolean enabled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.uuid = uuid;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
