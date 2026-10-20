package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.entities.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoutePointMapper {

    public RoutePointDTO toDTO(RoutePoint routePoint) {
        if (routePoint == null) {
            return null;
        }
        return new RoutePointDTO(routePoint.getUuid(), routePoint.getName(), routePoint.getLatitude(), routePoint.getLongitude());
    }

    public List<RoutePointDTO> toDTOList(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
