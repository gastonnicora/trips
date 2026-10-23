package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceCreate;
import com.gastonnicora.trips.services.SectionPriceService;
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
@Tag(name = "", description = "")
public class SectionPriceController {

    private final SectionPriceService sectionPriceService;
    public SectionPriceController(SectionPriceService sectionPriceService) {
        this.sectionPriceService = sectionPriceService;
    }
    /**
     * Registra el precio de un tramo entre dos puntos de una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada. Los tramos deben formar una cadena continua desde el
     * primer punto de la ruta, sin superposiciones ni huecos.
     * </p>
     *
     * @param companyUuid        Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid          Identificador único de la ruta a la que se asignará el tramo.
     * @param sectionPriceCreate Datos del tramo que se desea crear ({@link SectionPriceCreate}).
     *
     * @return datos del tramo creado.
     *
     * @see SectionPriceService#createSectionPrice(UUID, UUID, SectionPriceCreate)
     */
    @PostMapping("/companies/{companyUuid}/route/{routeUuid}/sectionPrice")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Agregar precio de tramo",
            description = "Asigna el precio de un tramo entre dos puntos de una ruta de una empresa."
    )
    public SectionPriceDTO createSectionPrice(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @RequestBody @Valid SectionPriceCreate sectionPriceCreate
                                             ) {
        return sectionPriceService.createSectionPrice(companyUuid, routeUuid, sectionPriceCreate);
    }

    /**
     * Obtiene el precio de un tramo de una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro
     * de la empresa indicada. Solo se devuelven tramos activos, de rutas activas
     * de empresas activas.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el tramo.
     * @param sectionUuid Identificador único del precio por tramo ({@code uuid} en la URL).
     *
     * @return datos del tramo solicitado.
     *
     * @see SectionPriceService#getSectionPrice(UUID, UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}/sectionPrice/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Obtener precio de tramo",
            description = "Obtiene el precio de un tramo entre dos puntos de una ruta de una empresa."
    )
    public SectionPriceDTO getSectionPrice(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid,
            @PathVariable("uuid") UUID sectionUuid
                                          ) {
        return sectionPriceService.getSectionPrice(companyUuid, routeUuid, sectionUuid);
    }

    /**
     * Obtiene los precios por tramo activos de una ruta.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN} dentro de
     * la empresa indicada. Solo se devuelven tramos activos, de rutas activas de
     * empresas activas. Si la ruta no tiene tramos activos, la respuesta es 404.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta cuyos tramos se desean obtener.
     *
     * @return lista de tramos activos de la ruta.
     *
     * @see SectionPriceService#getSectionPrices(UUID, UUID)
     */
    @GetMapping("/companies/{companyUuid}/route/{routeUuid}/sectionPrice")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Obtener precios de tramos",
            description = "Obtiene los precios por tramo activos de una ruta de una empresa."
    )
    public List<SectionPriceDTO> getSectionPrices(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("routeUuid") UUID routeUuid
                                                 ) {
        return sectionPriceService.getSectionPrices(companyUuid, routeUuid);
    }
}
