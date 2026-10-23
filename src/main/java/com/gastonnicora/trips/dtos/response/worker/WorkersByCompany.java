package com.gastonnicora.trips.dtos.response.worker;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO utilizado para representar una empresa y los trabajadores asociados a
 * ella.
 *
 * <p>
 * Contiene la información de la empresa y la lista de trabajadores
 * correspondientes a la misma.
 * </p>
 *
 * <p>
 * Se utiliza para exponer información de una empresa junto con sus trabajadores
 * en respuestas de la API.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-06-04
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "DTO utilizado para representar una empresa y sus trabajadores asociados.")
public class WorkersByCompany {

    /**
     * Empresa asociada a los trabajadores.
     */
    @Schema(description = "Empresa asociada a los trabajadores.")
    private CompanyDTO company;

    /**
     * Lista de trabajadores asociados a la empresa.
     */
    @Schema(description = "Lista de trabajadores asociados a la empresa.")
    private List<WorkerUser> workers;

    /**
     * Constructor por defecto que inicializa la lista de trabajadores vacía.
     */
    public WorkersByCompany() {
        this.workers = List.of();
    }
}
