package com.gastonnicora.trips.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.entities.Bus;

/**
 * Mapper que convierte entidades {@link Bus} a {@link BusDTO}.
 * <p>
 * Se utiliza para exponer datos de autobús de manera segura en la API,
 * sin incluir información sensible.
 * </p>
 */
@Component
public class BusMapper {
    private final CompanyMapper companyMapper;

    public BusMapper(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    /**
     * Convierte un {@link Bus} en {@link BusDTO}.
     * 
     * @param bus Entidad de autobús
     * @return DTO de autobús correspondiente
     */
    public BusDTO toDTO(Bus bus) {

        return new BusDTO(
                bus.getUuid().toString(),
                companyMapper.toDTO(bus.getCompany()),
                bus.getPlate(),
                bus.getModel(),
                bus.getCapacity(),
                bus.getCreatedAt().toString(),
                bus.getUpdatedAt().toString(),
                bus.isActive());
    }
 
    /**
     * Convierte una lista de {@link Bus} en una lista de {@link BusDTO}.
     * 
     * @param buses Lista de entidades de autobús
     * @return Lista de DTOs de autobús correspondientes
     */
    public List<BusDTO> toDTOList(List<Bus> buses) {
        return buses.stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
