package com.gastonnicora.trips.controllers.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.services.CompanyService;

import io.swagger.v3.oas.annotations.tags.Tag;

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
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies();
    }

}
