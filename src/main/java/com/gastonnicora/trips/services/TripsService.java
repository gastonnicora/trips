package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio encargado de gestionar los viajes programados dentro de una ruta.
 *
 * <p>
 * Coordina el acceso a los repositorios de rutas, viajes, vehículos y usuarios,
 * y convierte las entidades {@link Trip} en
 * {@link TripDTO} mediante {@link TripMapper}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Service
public class TripsService {

    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;


    /**
     * Crea una instancia del servicio de viajes.
     *
     * @param routeRepository   repositorio de rutas ({@link RouteRepository}).
     * @param tripRepository    repositorio de viajes ({@link TripRepository}).
     * @param vehicleRepository repositorio de vehículos ({@link VehicleRepository}).
     * @param userRepository    repositorio de usuarios ({@link UserRepository}).
     * @param tripMapper        mapper de viajes ({@link TripMapper}).
     */
    public TripsService(
            RouteRepository routeRepository, TripRepository tripRepository, VehicleRepository vehicleRepository,
            UserRepository userRepository, TripMapper tripMapper
                       ) {
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.tripMapper = tripMapper;
    }

    //public TripDTO createTrip() {return new TripDTO();}
}
