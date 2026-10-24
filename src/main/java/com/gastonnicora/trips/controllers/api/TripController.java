package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.dtos.request.trip.TripSearchRequest;
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

import java.util.List;
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

    /**
     * Busca los viajes activos de un día cuyo recorrido pasa cerca de un origen
     * y luego cerca de un destino.
     *
     * <p>
     * Un viaje coincide cuando su recorrido pasa a una distancia menor o igual a
     * {@code maxDistanceMeters} tanto del origen como del destino, y el punto más
     * cercano al origen aparece antes que el más cercano al destino, es decir, se
     * recorre en ese sentido. Solo se consideran los viajes cuya fecha de salida
     * cae dentro del día indicado.
     * </p>
     *
     * <p>
     * Requiere un usuario autenticado. Si algún parámetro falta o no es válido,
     * responde con HTTP 400 y los errores agrupados por campo.
     * </p>
     *
     * @param request parámetros de búsqueda ({@link TripSearchRequest}), recibidos
     *                como query params y validados.
     *
     * @return lista de {@link TripDTO} de los viajes que cumplen las condiciones;
     * vacía si no hay coincidencias.
     *
     * @see TripsService#getTripsPassingBetween(com.gastonnicora.trips.utils.GeoUtils.Coordinate,
     * com.gastonnicora.trips.utils.GeoUtils.Coordinate, double, java.time.LocalDate)
     */
    @GetMapping("/trips/search")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Buscar viajes entre un origen y un destino",
            description = "Devuelve los viajes de un día cuyo recorrido pasa cerca del origen y luego cerca del destino."
    )
    public List<TripDTO> getTripsPassingBetween(@Valid @ModelAttribute TripSearchRequest request) {
        return tripsService.getTripsPassingBetween(
                request.toOrigin(),
                request.toDestination(),
                request.getMaxDistanceMeters(),
                request.getDate());
    }
}