package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.entities.SectionPrice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente encargado de convertir entidades {@link SectionPrice} en objetos
 * {@link SectionPriceDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
@Component
public class SectionPriceMapper {

    private final RoutePointMapper routePointMapper;

    /**
     * Crea una instancia del mapper de precios por tramo.
     *
     * @param routePointMapper Mapper ({@link RoutePointMapper}) utilizado para
     *                         convertir los puntos de origen y destino del tramo.
     */
    public SectionPriceMapper(RoutePointMapper routePointMapper) {
        this.routePointMapper = routePointMapper;
    }

    /**
     * Convierte una entidad {@link SectionPrice} en un {@link SectionPriceDTO}.
     *
     * <p>
     * La conversión incluye el punto de origen, el punto de destino y el precio
     * del tramo.
     * </p>
     *
     * @param sectionPrice Entidad de precio por tramo que se desea convertir.
     *
     * @return DTO de precio por tramo correspondiente a la entidad proporcionada, o
     * {@code null} si la entidad es {@code null}.
     */
    public SectionPriceDTO toDTO(SectionPrice sectionPrice) {
        if (sectionPrice == null) {
            return null;
        }
        return new SectionPriceDTO(
                sectionPrice.getUuid(), routePointMapper.toDTO(sectionPrice.getFromPoint()),
                routePointMapper.toDTO(sectionPrice.getToPoint()), sectionPrice.getPrice(),
                sectionPrice.getCreatedAt(), sectionPrice.getUpdatedAt(), sectionPrice.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link SectionPrice} en una lista de
     * {@link SectionPriceDTO}.
     *
     * @param sectionPrices Lista de entidades de precio por tramo que se desea convertir.
     *
     * @return Lista de DTOs de precio por tramo correspondientes a las entidades
     * proporcionadas.
     */
    public List<SectionPriceDTO> toDTOList(List<SectionPrice> sectionPrices) {
        return sectionPrices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
