package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class TripsService {

    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;


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

    public TripDTO createTrip() {return  new TripDTO();}
}
