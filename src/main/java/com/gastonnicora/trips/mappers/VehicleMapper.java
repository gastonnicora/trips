package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.entities.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componente encargado de convertir entidades {@link Vehicle} en objetos
 * {@link VehicleDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-09-06
 */
@Component
public class VehicleMapper {

    private final CompanyMapper companyMapper;

    /**
     * Crea una instancia del mapper de vehículos.
     *
     * @param companyMapper Mapper ({@link CompanyMapper}) utilizado para convertir la empresa asociada al
     *                      vehículo.
     */
    public VehicleMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    /**
     * Convierte una entidad {@link Vehicle} en un {@link VehicleDTO}.
     *
     * <p>
     * La conversión incluye los datos de identificación, empresa asociada,
     * información del vehículo, capacidad, fechas de creación y actualización,
     * y estado.
     * </p>
     *
     * @param vehicle Entidad de vehículo que se desea convertir.
     *
     * @return DTO de vehículo correspondiente a la entidad proporcionada.
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
                vehicle.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link Vehicle} en una lista de
     * {@link VehicleDTO}.
     *
     * @param vehicles Lista de entidades de vehículos que se desea convertir.
     *
     * @return Lista de DTOs de vehículos correspondientes a las entidades
     * proporcionadas.
     */
    public List<VehicleDTO> toDTOList(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}