package com.gastonnicora.trips.controllers.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.services.CompanyService;

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

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies();
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
     *                      nueva empresa.
     * @return {@link CompanyDTO} con los datos de la empresa recién creada.
     * @see CompanyService #createCompany(CompanyCreate)
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public CompanyDTO postMethodName(@Valid @RequestBody CompanyCreate company) {
        return companyService.createCompany(company);
    }

}
