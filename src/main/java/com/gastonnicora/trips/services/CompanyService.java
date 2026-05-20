package com.gastonnicora.trips.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;

/**
 * Servicio de gestión de empresas.
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * empresas,
 * como la creación, actualización, eliminación, y obtención de empresas.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Service
public class CompanyService {
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * Constructor que inicializa los servicios necesarios para la gestión de
     * empresas.
     * 
     * @param userService       Servicio de gestión de usuarios.
     * @param companyRepository Repositorio de empresas utilizado para acceder a
     *                          la base de datos.
     * @param companyMapper     Mapper para convertir entidades {@link Company} a
     *                          DTOs {@link CompanyDTO}.
     */
    public CompanyService(UserService userService, CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public List<CompanyDTO> getCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companyMapper.toDTOList(companies);
    }

}
