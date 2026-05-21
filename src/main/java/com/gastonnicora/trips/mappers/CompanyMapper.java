package com.gastonnicora.trips.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.entities.Company;

/**
 * Mapper que convierte entidades {@link Company} a {@link CompanyDTO}.
 * <p>
 * Se utiliza para exponer datos de la empresa de manera segura en la API,
 * sin incluir información sensible como la contraseña del dueño.
 * </p>
 */
@Component
public class CompanyMapper {
    /**
     * Convierte un {@link Company} en {@link CompanyDTO}.
     * 
     * @param company Entidad de empresa
     * @return DTO de empresa correspondiente
     */
    public CompanyDTO toDTO(Company company) {
        return new CompanyDTO(
                company.getUuid(),
                company.getName(),
                new UserMapper().toDTO(company.getOwner()),
                company.getAddress(),
                company.getLatitude(),
                company.getLongitude(),
                company.getEmail(),
                company.getPhone(),
                company.getCreatedAt(),
                company.getUpdatedAt(),
                company.isActive());
    }

    /**
     * Convierte una lista de {@link Company} en una lista de {@link CompanyDTO}.
     * 
     * @param companies Lista de entidades de empresas
     * @return Lista de DTOs de empresas correspondientes
     */
    public List<CompanyDTO> toDTOList(List<Company> companies) {
        return companies.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
