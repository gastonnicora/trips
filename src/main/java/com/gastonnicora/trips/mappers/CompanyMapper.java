package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.entities.Company;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente encargado de convertir entidades {@link Company} en objetos
 * {@link CompanyDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Component
public class CompanyMapper {

    /**
     * Convierte una entidad {@link Company} en un {@link CompanyDTO}.
     *
     * <p>
     * La conversión incluye los datos de identificación, información de contacto,
     * ubicación, fechas de creación y actualización, y estado de la empresa.
     * </p>
     *
     * @param company Entidad de empresa que se desea convertir.
     *
     * @return DTO de empresa correspondiente a la entidad proporcionada.
     */
    public CompanyDTO toDTO(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanyDTO(
                company.getUuid(),
                company.getName(),
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
     * Convierte una lista de entidades {@link Company} en una lista de
     * {@link CompanyDTO}.
     *
     * @param companies Lista de entidades de empresas que se desea convertir.
     *
     * @return Lista de DTOs de empresas correspondientes a las entidades
     * proporcionadas.
     */
    public List<CompanyDTO> toDTOList(List<Company> companies) {
        return companies.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
