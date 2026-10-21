package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST encargado de gestionar las rutas de las empresas.
 *
 * <p>
 * Proporciona operaciones para registrar rutas pertenecientes a una empresa.
 * </p>
 *
 * <p>
 * Las operaciones requieren autorización sobre la empresa asociada. Los
 * usuarios con rol {@code OWNER}, {@code ADMIN} o {@code HR_MANAGER} dentro
 * de la empresa pueden gestionar sus rutas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Route", description = "Gestión de rutas")
public class RouteController {
    private final RouteService routeService;
    private final CompanyService companyService;

    /**
     * Crea una instancia del controlador de rutas.
     *
     * @param routeService   servicio encargado de la gestión de rutas ({@link RouteService}).
     * @param companyService servicio encargado de consultar las empresas ({@link CompanyService}).
     */
    public RouteController(RouteService routeService, CompanyService companyService) {
        this.routeService = routeService;
        this.companyService = companyService;
    }

    /**
     * Registra una nueva ruta asociada a una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que se asociará la ruta
     * @param routeCreate ({@link RouteCreate}) datos necesarios para crear la ruta
     *
     * @return datos de la ruta creada
     *
     * @see CompanyService#getCompanyEntity(UUID)
     * @see RouteService#createRoute(Company, RouteCreate)
     */
    @PostMapping("/companies/{companyUuid}/route")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Agregar ruta", description = "Agrega una ruta de una empresa.")
    public RouteDTO createRoute(
            @PathVariable("companyUuid") UUID companyUuid,
            @RequestBody @Valid RouteCreate routeCreate
                               ) {

        Company company = companyService.getCompanyEntity(companyUuid);
        return routeService.createRoute(
                company,
                routeCreate
                                       );
    }

    /**
     * Obtiene una ruta activa de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN}
     * dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid      UUID de la empresa a la que pertenece la ruta
     * @param routeUuid UUID de la ruta que se desea obtener
     *
     * @return datos de la ruta encontrada
     *
     * @see RouteService#getRoute(UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Obtener ruta", description = "Obtiene una ruta de una empresa por su uuid.")
    public RouteDTO getRoute(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid
                            ) {
        return routeService.getRoute(companyUuid, routeUuid);
    }


    /**
     * Obtiene las rutas activas de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN}
     * dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa de la que se desean obtener las rutas
     *
     * @return Lista de rutas activas de la empresa
     *
     * @see RouteService#getRoutes(UUID)
     */
    @GetMapping("/companies/{companyUuid}/routes")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Obtener rutas", description = "Obtiene las rutas activas de una empresa.")
    public List<RouteDTO> getRoutes(
            @PathVariable("companyUuid") UUID companyUuid
                                   ) {
        return routeService.getRoutes(companyUuid);
    }

    /**
     * Actualiza una ruta asociada a una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN}
     * dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid   UUID de la ruta que se desea actualizar.
     * @param routeUpdate ({@link RouteCreate}) datos necesarios para actualizar la ruta
     *
     * @return datos de la ruta actualizada
     *
     * @see RouteService#updateRoute(UUID, UUID, RouteCreate)
     */
    @PutMapping("/companies/{companyUuid}/route/{routeUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Actualizar ruta", description = "Actualiza el nombre y el tipo de precio de una ruta de una empresa.")
    public RouteDTO updateRoute(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @RequestBody @Valid RouteCreate routeUpdate
                               ) {

        return routeService.updateRoute(
                companyUuid,
                routeUuid,
                routeUpdate
                                       );
    }
}
