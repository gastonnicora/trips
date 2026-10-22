package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.services.RoutePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
 * usuarios con rol {@code OWNER} o {@code ADMIN} dentro
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

    /**
     * Crea una instancia del controlador de puntos de rutas.
     *
     * @param routePointService servicio encargado de la gestión de puntos de rutas ({@link RoutePointService}).
     */
    public RoutePointController(RoutePointService routePointService) {
        this.routePointService = routePointService;
    }

    /**
     * Registra un nuevo punto de ruta asociado a una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid      UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid        UUID de la ruta a la que se le va a agregar el punto.
     * @param routePointCreate ({@link RoutePointCreate}) datos necesarios para crear el punto de ruta.
     *
     * @return datos del punto de ruta creado.
     *
     * @see RoutePointService#createRoutePoint(UUID, UUID, RoutePointCreate)
     */
    @PostMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Agregar punto de ruta",
            description = "Agrega un punto de ruta a una ruta de una empresa.")
    public RoutePointDTO createRoutePoint(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @RequestBody @Valid RoutePointCreate routePointCreate
                                         ) {
        return routePointService.createRoutePoint(companyUuid, routeUuid, routePointCreate);
    }

    /**
     * Obtiene un punto de ruta de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * Solo se devuelven puntos activos que pertenezcan a la ruta indicada, y la ruta debe
     * pertenecer a la empresa indicada y estar activa.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid   UUID de la ruta a la que pertenece el punto.
     * @param uuid        UUID del punto de ruta que se desea obtener.
     *
     * @return datos del punto de ruta encontrado.
     *
     * @see RoutePointService#getRoutePoint(UUID, UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Obtener punto de ruta",
            description = "Obtiene un punto de ruta de una ruta de una empresa.")
    public RoutePointDTO getRoutePoint(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid
                                      ) {
        return routePointService.getRoutePoint(companyUuid, routeUuid, uuid);
    }

    /**
     * Obtiene todos los puntos de ruta activos de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * La ruta debe pertenecer a la empresa y estar activa, y solo se devuelven los puntos activos.
     * Si la ruta no tiene puntos activos, se responde con 404.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid   UUID de la ruta cuyos puntos se desean obtener.
     *
     * @return lista de puntos de ruta activos de la ruta.
     *
     * @see RoutePointService#getRoutePoints(UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Obtener puntos de ruta",
            description = "Obtiene los puntos activos de una ruta de una empresa.")
    public List<RoutePointDTO> getRoutePoints(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid
                                             ) {
        return routePointService.getRoutePoints(companyUuid, routeUuid);
    }

    /**
     * Actualiza el nombre y las coordenadas de un punto de ruta activo.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * Solo se actualizan puntos activos que pertenezcan a una ruta activa de la empresa.
     * Si el punto no existe o no cumple esas condiciones, se responde con 404.
     * </p>
     *
     * @param companyUuid      UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid        UUID de la ruta a la que pertenece el punto.
     * @param uuid             UUID del punto de ruta que se desea actualizar.
     * @param routePointUpdate ({@link RoutePointCreate}) nuevos datos del punto de ruta.
     *
     * @return datos del punto de ruta actualizado.
     *
     * @see RoutePointService#updateRoutePoint(UUID, UUID, UUID, RoutePointCreate)
     */
    @PutMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Actualizar punto de ruta",
            description = "Actualiza el nombre y las coordenadas de un punto activo de una ruta de una empresa.")
    public RoutePointDTO updateRoutePoint(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid RoutePointCreate routePointUpdate
                                         ) {
        return routePointService.updateRoutePoint(companyUuid, routeUuid, uuid, routePointUpdate);
    }

    /**
     * Da de baja un punto de ruta activo.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * La baja es lógica: el punto se marca como inactivo y deja de aparecer en las consultas.
     * Si el punto no existe, ya está inactivo o no pertenece a una ruta activa de la empresa,
     * se responde con 404.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid   UUID de la ruta a la que pertenece el punto.
     * @param uuid        UUID del punto de ruta que se desea dar de baja.
     *
     * @see RoutePointService#deleteRoutePoint(UUID, UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/route/{routeUuid}/routePoint/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Eliminar punto de ruta",
            description = "Da de baja un punto activo de una ruta de una empresa.")
    public void deleteRoutePoint(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid
                                ) {
        routePointService.deleteRoutePoint(companyUuid, routeUuid, uuid);
    }
}