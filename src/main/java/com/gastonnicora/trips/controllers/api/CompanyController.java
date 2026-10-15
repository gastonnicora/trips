package com.gastonnicora.trips.controllers.api;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.services.CompanyService;
import com.gastonnicora.trips.services.UserService;
import com.gastonnicora.trips.services.WorkerService;
import com.gastonnicora.trips.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

/**
 * Controlador REST encargado de la gestión de empresas.
 *
 * <p>
 * Proporciona operaciones para crear, consultar, modificar y eliminar empresas,
 * así como para obtener las empresas asociadas a un usuario.
 * </p>
 *
 * <p>
 * La gestión de empresas se realiza mediante {@link CompanyService}. Los
 * servicios {@link WorkerService} y {@link UserService} se utilizan para
 * gestionar las relaciones entre empresas, propietarios y usuarios.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company", description = "Gestión de empresas")
public class CompanyController {

    private final CompanyService companyService;
    private final WorkerService workerService;
    private final UserService userService;

    /**
     * Crea una instancia del controlador de empresas.
     *
     * @param companyService servicio encargado de la gestión de empresas ({@link CompanyService}).
     * @param workerService  servicio encargado de gestionar trabajadores y
     *                       propietarios de empresas ({@link WorkerService}).
     * @param userService    servicio encargado de consultar y gestionar usuarios ({@link  UserService}).
     */
    public CompanyController(CompanyService companyService, WorkerService workerService, UserService userService) {
        this.companyService = companyService;
        this.workerService = workerService;
        this.userService = userService;
    }

    /**
     * Crea una nueva empresa y registra al usuario autenticado como propietario.
     *
     * <p>
     * La empresa se crea a partir de los datos recibidos y posteriormente se
     * registra al usuario autenticado como propietario mediante un trabajador
     * asociado a la empresa.
     * </p>
     *
     * @param companyCreate datos necesarios para crear la empresa({@link CompanyCreate}).
     *
     * @return datos de la empresa creada
     *
     * @see UserService#getUser(UUID)
     * @see CompanyService#createCompany(CompanyCreate)
     * @see CompanyService#getCompanyEntity(UUID)
     * @see WorkerService#createWorkerOwner(User, Company)
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear empresa", description = "Crea una nueva empresa para el usuario autenticado y lo registra como propietario.")
    public CompanyDTO createCompany(@Valid @RequestBody CompanyCreate companyCreate) {
        User currentUser = userService.getUser(getCurrentUserUuid());

        CompanyDTO companyDTO = companyService.createCompany(companyCreate);
        Company company = companyService.getCompanyEntity(companyDTO.getUuid());

        workerService.createWorkerOwner(currentUser, company);
        return companyDTO;
    }

    /**
     * Elimina una empresa existente.
     *
     * <p>
     * Esta operación requiere que el usuario tenga el rol {@code OWNER} sobre
     * la empresa indicada.
     * </p>
     *
     * @param uuid UUID de la empresa que se desea eliminar
     *
     * @see CompanyService#deleteCompany(UUID)
     */
    @DeleteMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasRole(#uuid, T(com.gastonnicora.trips.enums.RoleCompany).OWNER)")
    @Operation(summary = "Eliminar empresa", description = "Elimina una empresa. El usuario debe tener rol OWNER sobre la empresa.")
    public void deleteCompany(@PathVariable("uuid") UUID uuid) {
        companyService.deleteCompany(uuid);
    }

    /**
     * Obtiene las empresas cuyo propietario es el usuario autenticado.
     *
     * @return respuesta con la lista de empresas asociadas al usuario actual
     *
     * @see SecurityUtils#getCurrentUserUuid()
     * @see UserService#getUser(UUID)
     * @see WorkerService#getCompaniesByOwner(UUID)
     * @see CompanyService#toListResponse(List)
     *
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener mis empresas", description = "Obtiene las empresas asociadas al usuario autenticado.")
    public ListResponse<CompanyDTO> getCompaniesByCurrentUser() {
        UUID uuid = getCurrentUserUuid();
        userService.getUser(uuid);
        List<Company> companies = workerService.getCompaniesByOwner(uuid);
        return companyService.toListResponse(companies);
    }

    /**
     * Obtiene las empresas cuyo propietario corresponde al usuario indicado.
     *
     * <p>
     * Esta operación requiere los roles {@code ADMIN} o {@code SUPER_ADMIN}.
     * </p>
     *
     * @param uuid UUID del usuario propietario de las empresas
     *
     * @return respuesta con la lista de empresas asociadas al usuario
     *
     * @see UserService#getUser(UUID)
     * @see WorkerService#getCompaniesByOwner(UUID)
     * @see CompanyService#toListResponse(List)
     */
    @GetMapping("/owner/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Obtener empresas de un usuario", description = "Obtiene las empresas asociadas a un usuario mediante su UUID. "
            + "Requiere los roles ADMIN o SUPER_ADMIN.")
    public ListResponse<CompanyDTO> getCompaniesByUser(@PathVariable UUID uuid) {
        userService.getUser(uuid);
        List<Company> companies = workerService.getCompaniesByOwner(uuid);
        return companyService.toListResponse(companies);
    }

    /**
     * Obtiene una empresa a partir de su UUID.
     *
     * @param uuid UUID de la empresa que se desea consultar
     *
     * @return datos de la empresa solicitada
     *
     * @see CompanyService#getCompany(UUID)
     */
    @GetMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener empresa", description = "Obtiene los datos de una empresa a partir de su UUID.")
    public CompanyDTO getCompany(@PathVariable UUID uuid) {
        return companyService.getCompany(uuid);
    }

    /**
     * Actualiza los datos de una empresa existente.
     *
     * <p>
     * El usuario debe tener permisos de propietario o administrador sobre la
     * empresa para realizar esta operación.
     * </p>
     *
     * @param uuid    UUID de la empresa que se desea actualizar
     * @param company nuevos datos de la empresa ({@link CompanyCreate}).
     *
     * @return datos de la empresa actualizada
     *
     * @see CompanyService#updateCompany(UUID, CompanyCreate)
     */
    @PutMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("""
                @companySecurity.hasAnyRole(
                    #uuid,
                    T(com.gastonnicora.trips.enums.RoleCompany).OWNER,
                    T(com.gastonnicora.trips.enums.RoleCompany).ADMIN
                )
            """)
    @Operation(summary = "Modificar empresa", description = "Actualiza los datos de una empresa. "
            + "El usuario debe tener rol OWNER o ADMIN sobre la empresa.")
    public CompanyDTO updateCompany(@PathVariable("uuid") UUID uuid, @Valid @RequestBody CompanyCreate company) {
        return companyService.updateCompany(uuid, company);
    }

    // TODO: Agregar endpoint para obtener todas las empresas
}