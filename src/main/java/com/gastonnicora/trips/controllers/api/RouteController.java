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

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Route", description = "Gestión de rutas")
public class RouteController {
    private final RouteService routeService;
    private final CompanyService companyService;

    public RouteController(RouteService routeService, CompanyService companyService) {
        this.routeService = routeService;
        this.companyService = companyService;
    }

    @PostMapping("/companies/{uuid}/route")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#uuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Agregar ruta", description = "Agrega una ruta de una empresa.")
    public RouteDTO createRoute(
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid RouteCreate routeCreate
                               ) {

        Company company = companyService.getCompanyEntity(uuid);
        return routeService.createRoute(
                company,
                routeCreate
                                       );
    }
}
