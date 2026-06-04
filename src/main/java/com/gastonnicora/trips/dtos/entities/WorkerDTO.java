package com.gastonnicora.trips.dtos.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.RoleCompany;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) que representa la información de la relación
 * trabajador empresa.
 * <p>
 * Se utiliza para exponer los datos de worker en respuestas de la API, sin
 * incluir información sensible como la contraseña.
 * </p>
 * 
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único de la relación.</li>
 * <li>{@code user}: {@link UserDTO} del worker.</li>
 * <li>{@code company}: {@link CompanyDTO} de la empresa.</li>
 * <li>{@code roles}: Conjunto de roles asignados al worker.</li>
 * <li>{@code active}: Estado del worker (activo o no).</li>
 * <li>{@code createdAt}: Fecha de creación del worker.</li>
 * <li>{@code updatedAt}: Fecha de última actualización del worker.</li>
 * </ul>
 * 
 * <p>
 * Ejemplo de JSON:
 * </p>
 * 
 * <pre>
 *{
 * "uuid": "550e8400-e29b-41d4-a716-446655440000",
 * "user": {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "Juan",
 *   "lastname": "Perez",
 *   "email": "juanperez@mail.com",
 *   "role": ["USER"],
 *   "enabled": true,
 *   "createdAt": "2026-01-01T00:00:00",
 *   "updatedAt": "2026-01-01T00:00:00"
 * },
 * "company": {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "Viajes LP",
 *   "address": "Calle Falsa 123,La Plata,Bs As, Argentina",
 *   "latitude": 500,
 *   "longitude": 5,
 *   "email": "company@mail.com",
 *   "phone": "+5491122334455",
 *   "createdAt": "2026-01-01T00:00:00",
 *   "updatedAt": "2026-01-01T00:00:00",
 *   "active": true
 * },
 * "roles": ["DRIVER"],
 * "active": true,
 * "createdAt": "2026-01-01T00:00:00",
 * "updatedAt": "2026-01-01T00:00:00"
 * }
 * </pre>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de relación trabajador empresa")
public class WorkerDTO {
    /**
     * Identificador único de la relación.
     */
    @Schema(description = "UUID de la relación", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID uuid;

    /**
     * {@link UserDTO} del worker.
     */
    @Schema(description = "DTO del usuario", implementation = UserDTO.class)
    private UserDTO user;
    
    /**
     * {@link CompanyDTO} de la empresa.
     */
    @Schema(description = "DTO de la empresa", implementation = CompanyDTO.class)
    private CompanyDTO company;
    
    /**
     * Conjunto de roles asignados al trabajador.
     */
    @Schema(description = "Roles del worker", example = "[\"DRIVER\"]")
    private Set<RoleCompany> roles;
    
    /**
     * Indica si el trabajador está activo.
     */
    @Schema(description = "Estado del worker", example = "true")
    private boolean active;
    
    /**
     * Fecha de creación del worker.
     */
    @Schema(description = "Fecha de creacion del worker", example = "2026-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    /**
     * Fecha de actualización del worker.
     */
    @Schema(description = "Fecha de actualizacion del worker", example = "2026-01-01T00:00:00")
    private LocalDateTime updatedAt;
}
