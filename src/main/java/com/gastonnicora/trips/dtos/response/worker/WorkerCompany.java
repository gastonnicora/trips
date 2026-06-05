package com.gastonnicora.trips.dtos.response.worker;

import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.enums.RoleCompany;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) que representa la información de la relación
 * usuraio empresa sin mostrar la informacion del usuario
 * 
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único de la relación.</li>
 * <li>{@code company}: {@link CompanyDTO} de la empresa.</li>
 * <li>{@code roles}: Conjunto de roles asignados al trabajador.</li>
 * <li>{@code active}: Estado del trabajador (activo o no).</li>
 * </ul>
 * <p>
 * Ejemplo de JSON:
 * </p>
 * 
 * <pre>
 *  {
 *   "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *   "company": {
 *     "uuid": "550e8400-e29b-41d4-a716-446655440000",
 *     "name": "Viajes LP",
 *     "address": "Calle Falsa 123,La Plata,Bs As, Argentina",
 *     "latitude": 500,
 *     "longitude": 500,
 *     "email": "company@mail.com",
 *     "phone": "+5491122334455",
 *     "createdAt": "2026-01-01T00:00:00",
 *     "updatedAt": "2026-01-01T00:00:00",
 *     "active": true
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
@Schema(description = "DTO de relación trabajador empresa sin información de usuario")
public class WorkerCompany {
    /**
     * Identificador único de la relación.
     */
    @Schema(description = "UUID de la relación")
    private UUID uuid;

    /**
     * {@link CompanyDTO} de la empresa.
     */
    @Schema(description = "DTO de la empresa")
    private CompanyDTO company;

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
