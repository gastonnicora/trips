package com.gastonnicora.trips.dtos.response.worker;

import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.RoleCompany;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) que representa la información de la relación
 * trabajador empresa sin mostrar la información del usuario.
 * <p>
 * Se utiliza para exponer los datos de trabajador en respuestas de la API, sin
 * incluir información sensible como la contraseña.
 * </p>
 * 
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único de la relación.</li>
 * <li>{@code user}: {@link UserDTO} del trabajador.</li>
 * <li>{@code roles}: Conjunto de roles asignados al trabajador.</li>
 * <li>{@code active}: Estado del trabajador (activo o no).</li>
 * </ul>
 * <p>
 * Ejemplo de JSON:
 * </p>
 * 
 * <pre>
 * {
 *   "uuid": "550e8400-e29b-41d4-a716-4466554400",
 *   "user": {
 *     "uuid": "550e8400-e29b-41d4-a716-4466554400",
 *     "name": "Juan",
 *     "lastname": "Perez",
 *     "email": "juanperez@mail.com",
 *     "role": ["USER"],
 *     "enabled": true,
 *     "createdAt": "2026-01-01T00:00:00",
 *     "updatedAt": "2026-01-01T00:00:00"
 *   },
 *   "roles": ["DRIVER"],
 *   "active": true
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de relación trabajador empresa sin información de la empresa")
public class WorkerUser {
    /**
     * Identificador único de la relación.
     */
    @Schema(description = "UUID de la relación")
    private UUID uuid;

    /**
     * {@link UserDTO} del trabajador.
     */
    @Schema(description = "Usuario asociado")
    private UserDTO user;

    /**
     * Conjunto de roles asignados al trabajador.
     */
    @Schema(description = "Roles dentro de la empresa")
    private Set<RoleCompany> roles;

    /**
     * Indica si el trabajador está activo.
     */
    @Schema(description = "Indica si el trabajador está activo")
    private boolean active;
}
