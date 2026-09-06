package com.gastonnicora.trips.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.entities.Vehicle;

/**
 * Mapper que convierte entidades {@link Vehicle} a {@link VehicleDTO}.
 * <p>
 * Se utiliza para exponer datos de vehículo de manera segura en la API, sin
 * incluir información sensible.
 * </p>
 */
@Component
public class VehicleMapper {

    private final CompanyMapper companyMapper;

    public VehicleMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    /**
     * Convierte un {@link Vehicle} en {@link VehicleDTO}.
     *
     * @param vehicle Entidad de vehículo
     * @return DTO de vehículo correspondiente
     */
    public VehicleDTO toDTO(Vehicle vehicle) {

        return new VehicleDTO(
                vehicle.getUuid(),
                companyMapper.toDTO(vehicle.getCompany()),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getCapacity(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                vehicle.isActive());
    }

    /**
     * Convierte una lista de {@link Vehicle} en una lista de {@link VehicleDTO}.
     *
     * @param vehicles Lista de entidades de vehículo
     * @return Lista de DTOs de vehículo correspondientes
     */
    public List<VehicleDTO> toDTOList(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
