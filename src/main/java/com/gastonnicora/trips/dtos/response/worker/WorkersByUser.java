package com.gastonnicora.trips.dtos.response.worker;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO utilizado para representar un usuario y las empresas asociadas a su
 * relación laboral.
 *
 * <p>
 * Contiene la información del usuario y la lista de relaciones con las empresas
 * en las que trabaja.
 * </p>
 *
 * <p>
 * Se utiliza para exponer información de un usuario junto con sus relaciones
 * laborales en respuestas de la API.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-04
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO utilizado para representar un usuario y sus empresas asociadas.")
public class WorkersByUser {

    /**
     * Usuario asociado a las relaciones laborales.
     */
    @Schema(description = "Usuario asociado a las relaciones laborales.")
    private UserDTO user;

    /**
     * Lista de relaciones del usuario con las empresas en las que trabaja.
     */
    @Schema(description = "Lista de relaciones del usuario con las empresas en las que trabaja.")
    private List<WorkerCompany> workers;
}
