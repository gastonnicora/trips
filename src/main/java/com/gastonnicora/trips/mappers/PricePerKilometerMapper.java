package com.gastonnicora.trips.mappers;


import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.entities.PricePerKilometer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PricePerKilometerMapper {

    public PricePerKilometerDTO toDTO(PricePerKilometer price) {
       if (price == null) {
            return null;
        }
        return new PricePerKilometerDTO(price.getUuid(), price.getBaseKm(), price.getBasePrice(), price.getPricePerKm());
    }

    public List<PricePerKilometerDTO> toDTOList(List<PricePerKilometer> prices) {
        return prices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
