package com.gastonnicora.trips.dtos.request.worker;

import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.RoleCompany;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para solicitar la incorporación de un usuario como trabajador
 * de una empresa.
 *
 * <p>
 * Contiene el UUID del usuario que se desea asociar a la empresa y el conjunto
 * de roles que tendrá dentro de dicha empresa.
 * </p>
 *
 * <p>
 * Los campos son validados mediante Jakarta Bean Validation para garantizar que
 * se proporcione un usuario válido y al menos un rol para la relación laboral.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-07
 */
@Schema(description = "DTO utilizado para agregar un trabajador a una empresa")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkerCreate {

    /**
     * UUID del usuario que se desea agregar como trabajador.
     * <p>
     * El usuario debe existir previamente en el sistema y no puede ser
     * {@code null}.
     * </p>
     */
    @Schema(
            description = "UUID del usuario que se quiere agregar como trabajador",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    @NotNull(message = "El UUID del usuario no puede ser nulo")
    private UUID userUuid;

    /**
     * Roles que tendrá el usuario dentro de la empresa.
     * <p>
     * El trabajador debe tener al menos un rol asignado. Los roles disponibles
     * están definidos en {@link RoleCompany}.
     * </p>
     */
    @Schema(
            description = "Conjunto de roles que tendrá el trabajador dentro de la empresa",
            example = "[\"ADMIN\", \"DRIVER\"]"
    )
    @NotEmpty(message = "El trabajador debe tener al menos un rol asignado")
    private Set<RoleCompany> roles;

}
