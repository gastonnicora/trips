package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.dtos.request.pricePerKilometer.PricePerKilometerCreate;
import com.gastonnicora.trips.services.PricePerKilometerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST encargado de gestionar los precios por kilómetro de las rutas
 * de las empresas.
 *
 * <p>
 * Proporciona operaciones para crear, obtener, actualizar y dar de baja el
 * precio por kilómetro de una ruta perteneciente a una empresa.
 * </p>
 *
 * <p>
 * La lógica de negocio relacionada con los precios por kilómetro se delega en
 * {@link PricePerKilometerService}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-22
 */
@RestController
@RequestMapping("/api")
@Tag(name = "PricePerKilometer", description = "Gestión de precios por kilómetros")
public class PricePerKilometerController {

    private final PricePerKilometerService pricePerKilometerService;

    /**
     * Crea una instancia del controlador de precios por kilómetro.
     *
     * @param pricePerKilometerService servicio encargado de la gestión de precios
     *                                 por kilómetro ({@link PricePerKilometerService}).
     */
    public PricePerKilometerController(
            PricePerKilometerService pricePerKilometerService
                                      ) {
        this.pricePerKilometerService = pricePerKilometerService;
    }

    /**
     * Registra el precio por kilómetro de una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada.
     * </p>
     *
     * @param companyUuid             Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid               Identificador único de la ruta a la que se asignará el precio.
     * @param pricePerKilometerCreate Datos necesarios para crear el precio por kilómetro
     *                                ({@link PricePerKilometerCreate}).
     *
     * @return datos del precio por kilómetro creado.
     *
     * @see PricePerKilometerService#createPricePerKilometer(UUID, UUID, PricePerKilometerCreate)
     */
    @PostMapping("/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Agregar precio por kilómetro",
            description = "Asigna un precio por kilómetro a una ruta de una empresa."
    )
    public PricePerKilometerDTO createPricePerKilometer(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @RequestBody @Valid PricePerKilometerCreate pricePerKilometerCreate
                                                       ) {
        return pricePerKilometerService.createPricePerKilometer(companyUuid, routeUuid, pricePerKilometerCreate);
    }

    /**
     * Da de baja la configuración de precio por kilómetro de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada. La baja es lógica: la configuración se marca como
     * inactiva y no se elimina de la base de datos.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el precio.
     * @param uuid        Identificador único del precio por kilómetro que se desea dar de baja.
     *
     * @see PricePerKilometerService#deletePricePerKilometer(UUID, UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Eliminar precio por kilómetro",
            description = "Da de baja el precio por kilómetro de una ruta de una empresa."
    )
    public void deletePricePerKilometer(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid
                                       ) {
        pricePerKilometerService.deletePricePerKilometer(companyUuid, routeUuid, uuid);
    }

    /**
     * Obtiene el precio por kilómetro de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el precio.
     * @param uuid        Identificador único del precio por kilómetro que se desea obtener.
     *
     * @return datos del precio por kilómetro encontrado.
     *
     * @see PricePerKilometerService#getPricePerKilometer(UUID, UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Obtener precio por kilómetro",
            description = "Obtiene el precio por kilómetro de una ruta de una empresa."
    )
    public PricePerKilometerDTO getPricePerKilometer(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid
                                                    ) {
        return pricePerKilometerService.getPricePerKilometer(companyUuid, routeUuid, uuid);
    }

    /**
     * Actualiza la configuración de precio por kilómetro de una ruta de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada.
     * </p>
     *
     * @param companyUuid             Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid               Identificador único de la ruta a la que pertenece el precio.
     * @param uuid                    Identificador único del precio por kilómetro que se desea actualizar.
     * @param pricePerKilometerUpdate Nuevos datos del precio por kilómetro ({@link PricePerKilometerCreate}).
     *
     * @return datos del precio por kilómetro actualizado.
     *
     * @see PricePerKilometerService#updatePricePerKilometer(UUID, UUID, UUID, PricePerKilometerCreate)
     */
    @PutMapping("/companies/{companyUuid}/route/{routeUuid}/pricePerKilometer/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Actualizar precio por kilómetro",
            description = "Actualiza el precio por kilómetro de una ruta de una empresa."
    )
    public PricePerKilometerDTO updatePricePerKilometer(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid PricePerKilometerCreate pricePerKilometerUpdate
                                                       ) {
        return pricePerKilometerService.updatePricePerKilometer(companyUuid, routeUuid, uuid, pricePerKilometerUpdate);
    }
}