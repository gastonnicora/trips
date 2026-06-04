package com.gastonnicora.trips.controllers.api;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.services.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador para la gestión de empresas.
 * <p>
 * Este controlador maneja todas las operaciones relacionadas con las empresas,
 * como la creación, modificación, eliminación y obtención de empresas a través
 * de los endpoints definidos en la URL "/api/companies".
 * </p>
 * 
 * <p>
 * Este controlador utiliza el servicio {@link CompanyService} para realizar las
 * operaciones de negocio relacionadas con la gestión de empresas.
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

    /**
     * Constructor del controlador CompanyController.
     * 
     * @param companyService Servicio de empresa que maneja la lógica de negocio
     *                       relacionada con las empresas.
     */
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Crea una nueva empresa en el sistema.
     * <p>
     * Este endpoint crea una nueva empresa con los datos proporcionados. Se realiza
     * la validación de los datos antes de crear la empresa, y se verifica que la
     * dirección sea válida.
     * </p>
     * <p>
     * En caso que los datos no sean válidos, se lanzará una excepción de tipo
     * {@link ValidationException}.
     * En caso que la dirección no exista se lanzará una excepción de tipo
     * {@link BadRequestException}.
     *
     * </p>
     * <p>
     * Este endpoint hace uso del servicio {@link CompanyService} para crear la
     * empresa en la base de datos.
     * </p>
     * 
     * @param company ({@link CompanyCreate}) con los datos válidos para la
     *                nueva empresa.
     * @return {@link CompanyDTO} con los datos de la empresa recién creada.
     * @see CompanyService #createCompany(CompanyCreate)
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public CompanyDTO postMethodName(@Valid @RequestBody CompanyCreate company) {
        return companyService.createCompany(company);
    }

    /**
     * Obtiene los detalles de una empresa por su UUID.
     * <p>
     * Este endpoint obtiene los detalles de una empresa por su UUID.
     * </p>
     * 
     * @param uuid UUID de la empresa que se quiere obtener.
     * @return CompanyDTO con los detalles de la empresa.
     * @see CompanyService #getCompany(UUID)
     */
    @GetMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener empresa", description = "Obtiene los detalles de una empresa por su uuid")
    public CompanyDTO getCompany(@PathVariable UUID uuid) {
        return companyService.getCompany(uuid);
    }

    /**
     * Obtiene todas las empresas del usuario.
     * <p>
     * Este endpoint obtiene todas las empresas del usuario.
     * </p>
     * 
     * @param uuid UUID del usuario.
     * @return Lista de empresas del usuario.
     * @see CompanyService #getCompaniesByUser(UUID)
     */
    @GetMapping("/owner/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
     @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Obtener empresas del usuario", description = "Obtiene las empresas del usuario")
    public ListResponse<CompanyDTO> getCompaniesByUser(@PathVariable UUID uuid) {
        return companyService.getCompaniesByUser(uuid);
    }

    /**
     * Obtiene todas las empresas del usuario actual.
     * <p>
     * Este endpoint obtiene todas las empresas del usuario actual.
     * </p>
     * 
     * @return Lista de empresas del usuario actual.
     * @see CompanyService #getCompaniesByCurrentUser()
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener empresas del usuario actual", description = "Obtiene las empresas del usuario actual")
    public ListResponse<CompanyDTO> getCompaniesByCurrentUser() {
        return companyService.getCompaniesByCurrentUser();
    }

    /**
     * Actualiza los detalles de una empresa por su UUID.
     * <p>
     * Este endpoint actualiza los detalles de una empresa por su UUID.
     * </p>
     * <p>
     * Valida los datos antes de guardar los cambios.
     * </p>
     *
     * @param uuid    UUID de la empresa que se quiere actualizar.
     * @param company {@link CompanyCreate} con los nuevos datos de la empresa.
     * @return CompanyDTO con los detalles de la empresa actualizada.
     * @see CompanyService #updateCompany(UUID, CompanyCreate)
     */
    @PutMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("""
                @companySecurity.hasAnyRole(
                    #uuid,
                    T(com.gastonnicora.trips.enums.RoleCompany).OWNER,
                    T(com.gastonnicora.trips.enums.RoleCompany).COMPANY_ADMIN
                )
            """)
    @Operation(summary = "Modificar empresa", description = "Modifica una empresa por su uuid")
    public CompanyDTO updateCompany(@PathVariable("uuid") UUID uuid, @Valid @RequestBody CompanyCreate company) {
        return companyService.updateCompany(uuid, company);
    }

    /**
     * Elimina una empresa por su UUID.
     * <p>
     * Este endpoint elimina una empresa por su UUID.
     * </p>
     * 
     * @param uuid UUID de la empresa que se quiere eliminar.
     * @see CompanyService #deleteCompany(UUID)
     */
    @DeleteMapping("/{uuid}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@companySecurity.hasRole(#uuid, T(com.gastonnicora.trips.enums.RoleCompany).OWNER)")
    @Operation(summary = "Eliminar empresa", description = "Elimina una empresa por su uuid")
    public void deleteCompany(@PathVariable("uuid") UUID uuid) {
        companyService.deleteCompany(uuid);
    }

}
