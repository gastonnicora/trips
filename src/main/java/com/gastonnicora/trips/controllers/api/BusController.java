package com.gastonnicora.trips.controllers.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.services.BusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para la gestión de buses.
 */
@RestController
@RequestMapping("/api/buses")
@Tag(name = "Bus", description = "Gestión de buses")
public class BusController {

    private final BusService busService;

    /**
     *
     * Constructor del controlador BusController.
     *
     * @param BusService Servicio del Transporte
     */
    public BusController(BusService busService) {
        this.busService = busService;
    }

    /**
     * Obtiene los detalles de un transporte por su UUID.
     * <p>
     * Este endpoint obtiene los detalles de un transporte por su UUID.
     * </p>
     *
     * @param uuid UUID del transporte que se quiere obtener.
     * @return BusDTO con los detalles del transporte.
     * @see BusService#getBus(UUID)
     */
    @GetMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener transporte", description = "Obtiene los detalles de un transporte por su uuid")
    public BusDTO getBus(@PathVariable UUID uuid){
        return busService.getBus(uuid);
    }
}
