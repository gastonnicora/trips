package com.gastonnicora.trips.dtos.response.worker;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.enums.RoleCompany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * DTO utilizado para representar la relación entre un trabajador y una empresa,
 * incluyendo la información del usuario asociado.
 *
 * <p>
 * Contiene el identificador de la relación, la información del trabajador, los
 * roles asignados dentro de la empresa y el estado de la relación.
 * </p>
 *
 * <p>
 * No incluye información correspondiente a la empresa asociada.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-06-04
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para representar la relación entre un trabajador y una empresa sin información de la empresa.")
public class WorkerUser {

    /**
     * Identificador único de la relación entre el trabajador y la empresa.
     */
    @Schema(
            description = "Identificador único de la relación entre el trabajador y la empresa.",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uuid;

    /**
     * Usuario asociado a la relación laboral.
     */
    @Schema(
            description = "Usuario asociado a la relación laboral.",
            implementation = UserDTO.class
    )
    private UserDTO user;

    /**
     * Conjunto de roles asignados al trabajador dentro de la empresa.
     */
    @Schema(
            description = "Roles asignados al trabajador dentro de la empresa.",
            example = "[\"DRIVER\"]"
    )
    private Set<RoleCompany> roles;

    /**
     * Indica si la relación del trabajador con la empresa se encuentra activa.
     */
    @Schema(
            description = "Indica si la relación del trabajador con la empresa se encuentra activa.",
            example = "true"
    )
    private boolean active;
}
