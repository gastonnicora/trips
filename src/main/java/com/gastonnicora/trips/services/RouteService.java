package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    public RouteService(RouteRepository routeRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }

    public RouteDTO createRoute(Company company, RouteCreate routeCreate) {

        Route route = new Route(company, routeCreate.getName(), routeCreate.getPricingType());

        return routeMapper.toDTO(routeRepository.save(route));
    }
}
