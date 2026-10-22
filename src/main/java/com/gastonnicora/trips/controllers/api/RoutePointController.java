package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.services.RoutePointService;
import com.gastonnicora.trips.services.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST encargado de gestionar los puntos de rutas de las empresas.
 *
 * <p>
 * Proporciona operaciones para registrar puntos de rutas pertenecientes a una empresa.
 * </p>
 *
 * <p>
 * Las operaciones requieren autorización sobre la empresa asociada. Los
 * usuarios con rol {@code OWNER}, {@code ADMIN} dentro
 * de la empresa pueden gestionar sus puntos de rutas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-21
 */
@RestController
@RequestMapping("/api")
@Tag(name = "RoutePoint", description = "Gestión de puntos de rutas")
public class RoutePointController {

    private final RoutePointService routePointService;
    private final RouteService routeService;

    /**
     * Crea una instancia del controlador de puntos de rutas.
     *
     * @param routePointService servicio encargado de la gestión de puntos de rutas ({@link RoutePointService}).
     * @param routeService      servicio encargado de consultar las rutas ({@link RouteService}).
     */
    public RoutePointController(RoutePointService routePointService, RouteService routeService) {
        this.routePointService = routePointService;
        this.routeService = routeService;
    }

    /**
     * Registra un nuevo punto de ruta asociado a una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid      UUID de la empresa a la que se asociará la ruta.
     * @param routeUuid        UUID de la ruta a la que se le va a agregar el punto.
     * @param routePointCreate ({@link RouteCreate}) datos necesarios para crear la ruta.
     *
     * @return datos del punto de ruta creada
     *
     * @see RoutePointService#createRoutePoint(UUID,UUID, RoutePointCreate)
     */
    @PostMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Agregar ruta", description = "Agrega una ruta de una empresa.")
    public RoutePointDTO createRoutePoint(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @RequestBody @Valid RoutePointCreate routePointCreate
                                         ) {
        return routePointService.createRoutePoint(companyUuid,routeUuid, routePointCreate);
    }
}

