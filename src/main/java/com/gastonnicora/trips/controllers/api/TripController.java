package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.RouteService;
import com.gastonnicora.trips.services.TripsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Trip API", description = "Endpoints para la gestión de viajes")
public class TripController {

    private final RouteService routeService;
    private final TripsService tripsService;


    public TripController(RouteService routeService, TripsService tripsService) {
        this.routeService = routeService;
        this.tripsService = tripsService;
    }

    /**
     * Registra un nuevo viaje dentro de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de
     * la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece el viaje.
     * @param tripCreate  datos necesarios para crear el viaje ({@link TripCreate}).
     *
     * @return datos del viaje creado ({@link TripDTO}).
     *
     * @see TripsService#createTrip(UUID, TripCreate)
     */
    @PostMapping("/companies/{companyUuid}/trip")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(summary = "Agregar viaje", description = "Agrega un viaje a una empresa.")
    public TripDTO createTrip(
            @PathVariable("companyUuid") UUID companyUuid,
            @RequestBody @Valid TripCreate tripCreate
                             ) {
        return tripsService.createTrip(
                companyUuid,
                tripCreate);
    }

    /**
     * Obtiene un viaje activo por su identificador único.
     *
     * <p>
     * Requiere un usuario autenticado.
     * </p>
     *
     * @param uuid Identificador único del viaje.
     *
     * @return {@link TripDTO} correspondiente al viaje encontrado.
     *
     * @see TripsService#getTrip(UUID)
     */
    @GetMapping("/trip/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener viaje", description = "Obtiene un viaje a una empresa.")
    public TripDTO getTrip(
            @PathVariable("uuid") UUID uuid
                             ) {
        return tripsService.getTrip(uuid);
    }
}
