package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador REST encargado de la gestión de vehículos asociados a empresas.
 *
 * <p>
 * Proporciona operaciones para consultar, registrar y eliminar vehículos
 * pertenecientes a una empresa.
 * </p>
 *
 * <p>
 * Las operaciones requieren autorización sobre la empresa asociada. Los
 * usuarios con rol {@code OWNER} o {@code ADMIN} dentro de la empresa pueden
 * gestionar sus vehículos.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-06
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Vehicle", description = "Gestión de vehículos")
public class VehicleController {

    private final VehicleService vehicleService;
    private final CompanyService companyService;

    /**
     * Crea una instancia del controlador de vehículos.
     *
     * @param vehicleService servicio encargado de la gestión de vehículos
     * @param companyService servicio encargado de consultar las empresas
     */
    public VehicleController(VehicleService vehicleService, CompanyService companyService) {
        this.vehicleService = vehicleService;
        this.companyService = companyService;
    }

    /**
     * Obtiene un vehículo perteneciente a una empresa.
     *
     * <p>
     * El acceso requiere que el usuario autenticado tenga rol {@code OWNER} o
     * {@code ADMIN} dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece el vehículo
     * @param vehicleUuid UUID del vehículo que se desea consultar
     * @return datos del vehículo solicitado
     * @see VehicleService#getVehicle(UUID,UUID)
     */
    @GetMapping("/companies/{companyUuid}/vehicle/{vehicleUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Obtener vehículo",
            description = "Obtiene los datos de un vehículo perteneciente a una empresa."
    )
    public VehicleDTO getVehicle(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("vehicleUuid") UUID vehicleUuid) {

        return vehicleService.getVehicle(vehicleUuid,companyUuid);
    }

    /**
     * Registra un nuevo vehículo asociado a una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN}
     * dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid   UUID de la empresa a la que se asociará el vehículo
     * @param vehicleCreate datos necesarios para crear el vehículo
     * @return datos del vehículo creado
     * @see VehicleService#createVehicle(Company, VehicleCreate)
     */
    @PostMapping("/companies/{companyUuid}/vehicle")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Agregar vehículo",
            description = "Registra un nuevo vehículo y lo asocia a una empresa."
    )
    public VehicleDTO createVehicle(
            @PathVariable("companyUuid") UUID companyUuid,
            @RequestBody @Valid VehicleCreate vehicleCreate) {

        Company company = companyService.getCompanyEntity(companyUuid);
        return vehicleService.createVehicle(company, vehicleCreate);
    }

    /**
     * Elimina un vehículo de una empresa.
     *
     * <p>
     * El usuario autenticado debe tener rol {@code OWNER} o {@code ADMIN}
     * dentro de la empresa indicada.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece el vehículo
     * @param vehicleUuid UUID del vehículo que se desea eliminar
     * @see VehicleService#deleteVehicle(UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/vehicle/{vehicleUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN)")
    @Operation(
            summary = "Eliminar vehículo",
            description = "Elimina un vehículo de una empresa."
    )
    public void deleteVehicle(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("vehicleUuid") UUID vehicleUuid) {

        vehicleService.deleteVehicle(companyUuid, vehicleUuid);
    }
}