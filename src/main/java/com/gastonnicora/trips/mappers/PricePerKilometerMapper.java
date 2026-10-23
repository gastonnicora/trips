package com.gastonnicora.trips.mappers;


import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.entities.PricePerKilometer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente encargado de convertir entidades {@link PricePerKilometer} en
 * objetos {@link PricePerKilometerDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-20
 */
@Component
public class PricePerKilometerMapper {

    /**
     * Convierte una entidad {@link PricePerKilometer} en un
     * {@link PricePerKilometerDTO}.
     *
     * @param price Entidad de precio por kilómetro que se desea convertir.
     *
     * @return DTO de precio por kilómetro correspondiente a la entidad
     * proporcionada, o {@code null} si la entidad es {@code null}.
     */
    public PricePerKilometerDTO toDTO(PricePerKilometer price) {
        if (price == null) {
            return null;
        }
        return new PricePerKilometerDTO(
                price.getUuid(), price.getBaseKm(), price.getBasePrice(), price.getPricePerKm(), price.getCreatedAt(),
                price.getUpdatedAt(), price.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link PricePerKilometer} en una lista de
     * {@link PricePerKilometerDTO}.
     *
     * @param prices Lista de entidades de precio por kilómetro que se desea convertir.
     *
     * @return Lista de DTOs de precio por kilómetro correspondientes a las
     * entidades proporcionadas.
     */
    public List<PricePerKilometerDTO> toDTOList(List<PricePerKilometer> prices) {
        return prices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
