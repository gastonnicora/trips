package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las rutas asociadas a empresas.
 *
 * <p>
 * Permite crear rutas y convertirlas en su representación {@link RouteDTO}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    /**
     * Crea una instancia del servicio de rutas.
     *
     * @param routeRepository Repositorio utilizado para gestionar las rutas
     *                        ({@link RouteRepository}).
     * @param routeMapper     Mapper utilizado para convertir entidades
     *                        {@link Route} en {@link RouteDTO} ({@link RouteMapper}).
     */
    public RouteService(RouteRepository routeRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }

    /**
     * Crea una nueva ruta asociada a una empresa.
     *
     * <p>
     * La ruta se crea con el nombre y el tipo de precio indicados. Los puntos y
     * los precios de la ruta no se cargan en esta operación.
     * </p>
     *
     * @param company     Empresa({@link Company}) a la que se asociará la ruta.
     * @param routeCreate Datos de la ruta que se desea crear ({@link RouteCreate}).
     *
     * @return {@link RouteDTO} correspondiente a la ruta creada.
     *
     * @see RouteMapper#toDTO(Route)
     */
    public RouteDTO createRoute(Company company, RouteCreate routeCreate) {

        Route route = new Route(company, routeCreate.getName(), routeCreate.getPricingType());

        return routeMapper.toDTO(routeRepository.save(route));
    }
}
