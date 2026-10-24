package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.entities.Trip;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper que convierte entidades {@link Trip} en sus DTOs correspondientes.
 *
 * <p>
 * Delega la conversión de la ruta, el vehículo y el conductor en sus mappers
 * respectivos, para mantener la misma representación que en el resto de la API.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Component
public class TripMapper {

    private final RouteMapper routeMapper;
    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;

    /**
     * Construye el mapper con los mappers de las entidades relacionadas.
     *
     * @param routeMapper   mapper de rutas.
     * @param userMapper    mapper de usuarios.
     * @param vehicleMapper mapper de vehículos.
     */
    public TripMapper(RouteMapper routeMapper, UserMapper userMapper, VehicleMapper vehicleMapper) {
        this.routeMapper = routeMapper;
        this.userMapper = userMapper;
        this.vehicleMapper = vehicleMapper;
    }

    /**
     * Convierte una entidad {@link Trip} en un {@link TripDTO}.
     *
     * <p>
     * La ruta, el vehículo y el conductor se convierten con sus propios mappers.
     * La ruta puede incluir puntos y precios, por lo que acceder a estas
     * relaciones carga datos adicionales de la base.
     * </p>
     *
     * @param trip entidad de viaje que se desea convertir.
     *
     * @return DTO del viaje, o {@code null} si la entidad es {@code null}.
     */
    public TripDTO toDTO(Trip trip) {
        if (trip == null) {
            return null;
        }
        return new TripDTO(
                trip.getUuid(),
                routeMapper.toDTO(trip.getRoute()),
                vehicleMapper.toDTO(trip.getVehicle()),
                userMapper.toDTO(trip.getDriver()),
                trip.getDate(),
                trip.getCreatedAt(),
                trip.getUpdatedAt(),
                trip.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link Trip} en una lista de
     * {@link TripDTO}.
     *
     * @param trips lista de entidades de viajes que se desea convertir.
     *
     * @return lista de DTOs de viajes, o una lista vacía si la entrada es
     * {@code null}.
     */
    public List<TripDTO> toDTOList(List<Trip> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}