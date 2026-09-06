package com.gastonnicora.trips.dtos.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) que representa la información de un usuario.
 * <p>
 * Se utiliza para exponer los datos de usuario en respuestas de la API, sin
 * incluir información sensible como la contraseña.
 * </p>
 *
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del usuario.</li>
 * <li>{@code name}: Nombre del usuario.</li>
 * <li>{@code lastname}: Apellido del usuario.</li>
 * <li>{@code email}: Correo electrónico del usuario.</li>
 * <li>{@code role}: Conjunto de roles asignados al usuario.</li>
 * <li>{@code enabled}: Estado del usuario (habilitado o deshabilitado).</li>
 * <li>{@code createdAt}: Fecha y hora de creación del usuario.</li>
 * <li>{@code updatedAt}: Fecha y hora de última actualización.</li>
 * </ul>
 *
 * <p>
 * Ejemplo de JSON:
 * </p>
 *
 * <pre>
 * {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "Juan",
 *   "lastname": "Perez",
 *   "email": "juanperez@mail.com",
 *   "role": ["USER"],
 *   "enabled": true,
 *   "createdAt": "2026-01-01T00:00:00",
 *   "updatedAt": "2026-01-01T00:00:00"
 * }
 * </pre>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "DTO de usuario")
public class UserDTO {

    /**
     * Identificador único del usuario.
     */
    private UUID uuid;

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
     * Dirección de correo electrónico del usuario.
     */
    @Schema(description = "Email del usuario", example = "juanperez@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;

    /**
     * Roles asignados al usuario.
     */
    @Schema(description = "Roles del usuario", example = "[\"USER\"]")
    private Set<Role> role;

    /**
     * Indica si el usuario está habilitado o deshabilitado.
     */
    @Schema(description = "Estado del usuario", example = "true")
    private boolean enabled;

    /**
     * Fecha y hora de creación del usuario.
     */
    @Schema(description = "Fecha de creación del usuario", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del usuario.
     */
    @Schema(description = "Fecha de actualización del usuario", example = "2026-01-01T00:00:00")
    private LocalDateTime updatedAt;

    /**
     * Constructor completo del DTO de usuario.
     *
     * @param uuid Identificador único del usuario
     * @param name Nombre del usuario
     * @param lastname Apellido del usuario
     * @param email Correo electrónico del usuario
     * @param role ({@link Set})conjunto de {@link Role} asignados al usuario
     * @param enabled Estado del usuario (habilitado o no)
     * @param createdAt Fecha de creación
     * @param updatedAt Fecha de actualización
     */
    public UserDTO(UUID uuid, String name, String lastname, String email, Set<Role> role, boolean enabled,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
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
