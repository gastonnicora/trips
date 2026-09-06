package com.gastonnicora.trips.controllers.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.services.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para la gestión de vehículos.
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicle", description = "Gestión de vehículos")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     *
     * Constructor del controlador VehicleController.
     *
     * @param VehicleService Servicio del Transporte
     */
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Obtiene los detalles de un vehículo por su UUID.
     * <p>
     * Este endpoint obtiene los detalles de un vehículo por su UUID.
     * </p>
     *
     * @param uuid UUID del vehículo que se quiere obtener.
     * @return VehicleDTO con los detalles del vehículo.
     * @see VehicleService#getVehicle(UUID)
     */
    @GetMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener vehículo", description = "Obtiene los detalles de un vehículo por su uuid")
    public VehicleDTO getVehicle(@PathVariable UUID uuid) {
        return vehicleService.getVehicle(uuid);
    }
}
