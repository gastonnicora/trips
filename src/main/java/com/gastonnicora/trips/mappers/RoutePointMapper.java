package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.entities.RoutePoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente encargado de convertir entidades {@link RoutePoint} en objetos
 * {@link RoutePointDTO}.
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
public class RoutePointMapper {

    /**
     * Convierte una entidad {@link RoutePoint} en un {@link RoutePointDTO}.
     *
     * @param routePoint Entidad de punto de ruta que se desea convertir.
     *
     * @return DTO de punto de ruta correspondiente a la entidad proporcionada, o
     * {@code null} si la entidad es {@code null}.
     */
    public RoutePointDTO toDTO(RoutePoint routePoint) {
        if (routePoint == null) {
            return null;
        }
        return new RoutePointDTO(
                routePoint.getUuid(), routePoint.getName(), routePoint.getLatitude(), routePoint.getLongitude(),
                routePoint.getCreatedAt(), routePoint.getUpdatedAt(), routePoint.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link RoutePoint} en una lista de
     * {@link RoutePointDTO}.
     *
     * @param routePoints Lista de entidades de puntos de ruta que se desea convertir.
     *
     * @return Lista de DTOs de puntos de ruta correspondientes a las entidades
     * proporcionadas.
     */
    public List<RoutePointDTO> toDTOList(List<RoutePoint> routePoints) {
        return routePoints.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
