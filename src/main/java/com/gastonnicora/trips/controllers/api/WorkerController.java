package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.request.worker.WorkerCreate;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

/**
 * Controlador REST encargado de gestionar los trabajadores y sus relaciones con
 * las empresas.
 *
 * <p>
 * Permite consultar los trabajadores de una empresa, incorporarlos o
 * eliminarlos de una empresa y modificar sus roles dentro de ella. También
 * permite consultar las relaciones laborales del usuario autenticado.
 * </p>
 *
 * <p>
 * Las operaciones sobre una empresa requieren que el usuario autenticado posea
 * un rol con permisos suficientes dentro de dicha empresa.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-09-07
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Worker", description = "Gestión de trabajadores")
public class WorkerController {

    private final WorkerService workerService;
    private final CompanyService companyService;
    private final UserService userService;

    /**
     * Crea una instancia del controlador de trabajadores.
     *
     * @param workerService  ({@link WorkerService}) servicio encargado de la gestión de trabajadores.
     * @param companyService ({@link CompanyService}) servicio encargado de la gestión de empresas.
     * @param userService    ({@link UserService}) servicio encargado de la gestión de usuarios.
     */
    public WorkerController(
            WorkerService workerService,
            CompanyService companyService,
            UserService userService
                           ) {
        this.workerService = workerService;
        this.companyService = companyService;
        this.userService = userService;
    }

    /**
     * Agrega un usuario como trabajador de una empresa.
     *
     * <p>
     * El usuario se incorpora a la empresa con los roles indicados en la
     * solicitud.
     * </p>
     *
     * <p>
     * Requiere que el usuario autenticado tenga rol {@code OWNER},
     * {@code ADMIN} o {@code HR_MANAGER} dentro de la empresa.
     * </p>
     *
     * @param uuid         UUID de la empresa.
     * @param workerCreate datos del usuario y roles con los que se registrará
     *                     como trabajador ({@link WorkerCreate}).
     *
     * @return datos del trabajador creado
     *
     * @see CompanyService#getCompanyEntity(UUID)
     * @see UserService#getUser(UUID)
     * @see WorkerService#createWorker(User, Company, Set)
     */
    @PostMapping("/companies/{uuid}/worker")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#uuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Agregar trabajador", description = "Agrega un usuario como trabajador de una empresa.")
    public WorkerDTO createWorker(
            @PathVariable("uuid") UUID uuid,
            @RequestBody @Valid WorkerCreate workerCreate
                                 ) {

        Company company = companyService.getCompanyEntity(uuid);
        User user = userService.getUser(workerCreate.getUserUuid());

        return workerService.createWorker(
                user,
                company,
                workerCreate.getRoles()
                                         );
    }

    /**
     * Elimina un trabajador de una empresa.
     *
     * <p>
     * Requiere que el usuario autenticado tenga rol {@code OWNER},
     * {@code ADMIN} o {@code HR_MANAGER} dentro de la empresa.
     * </p>
     *
     * @param companyUuid UUID de la empresa
     * @param userUuid    UUID del usuario que se eliminará de la empresa
     *
     * @see WorkerService#deleteWorker(UUID, UUID)
     */
    @DeleteMapping("/companies/{companyUuid}/worker/{userUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Eliminar trabajador", description = "Elimina la relación laboral de un usuario con una empresa.")
    public void deleteWorker(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("userUuid") UUID userUuid
                            ) {

        workerService.deleteWorker(userUuid, companyUuid);
    }

    /**
     * Obtiene los trabajadores asociados a una empresa.
     *
     * <p>
     * Requiere que el usuario autenticado tenga rol {@code OWNER},
     * {@code ADMIN} o {@code HR_MANAGER} dentro de la empresa.
     * </p>
     *
     * @param uuid UUID de la empresa
     *
     * @return trabajadores asociados a la empresa
     *
     * @see WorkerService#getWorkersByCompany(UUID)
     */
    @GetMapping("/companies/{uuid}/workers")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#uuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Obtener trabajadores", description = "Obtiene los trabajadores asociados a una empresa.")
    public WorkersByCompany getWorkersByCompany(@PathVariable("uuid") UUID uuid) {
        return workerService.getWorkersByCompany(uuid);
    }

    /**
     * Obtiene las relaciones laborales del usuario autenticado.
     *
     * <p>
     * La respuesta contiene las empresas a las que pertenece el usuario y la
     * información asociada a su relación laboral.
     * </p>
     *
     * @return relaciones laborales del usuario autenticado
     *
     * @see WorkerService#getWorkersByUser(UUID)
     */
    @GetMapping("/users/workers")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener mis relaciones laborales", description = "Obtiene las empresas a las que pertenece el usuario "
            + "autenticado y sus roles dentro de cada una.")
    public WorkersByUser getWorkersByCurrentUser() {
        return workerService.getWorkersByUser(getCurrentUserUuid());
    }

    /**
     * Actualiza los roles de un trabajador dentro de una empresa.
     *
     * <p>
     * Los roles enviados reemplazan o actualizan los roles que el trabajador
     * posee actualmente dentro de la empresa, según la lógica implementada por
     * {@link WorkerService}.
     * </p>
     *
     * <p>
     * Requiere que el usuario autenticado tenga rol {@code OWNER},
     * {@code ADMIN} o {@code HR_MANAGER} dentro de la empresa.
     * </p>
     *
     * @param companyUuid  UUID de la empresa
     * @param userUuid     UUID del trabajador cuyos roles se modificarán
     * @param workerCreate ({@link WorkerCreate}) datos que contienen los nuevos roles del trabajador
     *
     * @return datos actualizados del trabajador
     *
     * @see WorkerService#updateWorker(UUID, UUID, java.util.Set)
     */
    @PutMapping("/companies/{companyUuid}/worker/{userUuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasAnyRole(#companyUuid, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).OWNER, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).ADMIN, "
            + "T(com.gastonnicora.trips.enums.RoleCompany).HR_MANAGER)")
    @Operation(summary = "Actualizar roles del trabajador", description = "Actualiza los roles de un trabajador dentro de una empresa.")
    public WorkerDTO updateWorkerRoles(
            @PathVariable("companyUuid") UUID companyUuid,
            @PathVariable("userUuid") UUID userUuid,
            @RequestBody @Valid WorkerCreate workerCreate
                                      ) {

        return workerService.updateWorker(
                userUuid,
                companyUuid,
                workerCreate.getRoles()
                                         );
    }
}