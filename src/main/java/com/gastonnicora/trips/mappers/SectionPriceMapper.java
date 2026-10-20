package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SectionPriceMapper {

    private final RoutePointMapper routePointMapper;

    public SectionPriceMapper(RoutePointMapper routePointMapper) {
        this.routePointMapper = routePointMapper;
    }

    public SectionPriceDTO toDTO(SectionPrice sectionPrice) {
        if (sectionPrice == null) {
            return null;
        }
        return new SectionPriceDTO(
                sectionPrice.getUuid(), routePointMapper.toDTO(sectionPrice.getFromPoint()),
                routePointMapper.toDTO(sectionPrice.getToPoint()), sectionPrice.getPrice()
        );
    }

    public List<SectionPriceDTO> toDTOList(List<SectionPrice>  sectionPrices) {
         return sectionPrices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
