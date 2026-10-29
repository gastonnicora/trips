package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.PassageDTO;
import com.gastonnicora.trips.dtos.request.passage.PassageCreate;
import com.gastonnicora.trips.services.PassageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

/**
 * Controlador de los pasajes de los viajes.
 *
 * <p>
 * El pasajero que reserva es el usuario autenticado: su identificador se toma del
 * token y no del cuerpo de la petición.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Passage API", description = "Endpoints para la reserva de pasajes")
public class PassageController {

    private final PassageService passageService;

    public PassageController(PassageService passageService) {
        this.passageService = passageService;
    }

    /**
     * Reserva un pasaje del usuario autenticado dentro de un viaje.
     *
     * @param passageCreate datos del pasaje: viaje, subida y bajada.
     *
     * @return datos del pasaje creado.
     *
     * @see PassageService#createPassage(java.util.UUID, PassageCreate)
     */
    @PostMapping("/passages")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Reservar pasaje",
            description = "Reserva un pasaje para el usuario autenticado en un viaje activo. "
                    + "El precio se calcula según la forma de cobro de la ruta."
    )
    public PassageDTO createPassage(@RequestBody @Valid PassageCreate passageCreate) {
        return passageService.createPassage(getCurrentUserUuid(), passageCreate);
    }
}