package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.dtos.request.route.RouteCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RouteMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    /**
     * Obtiene una ruta activa de una empresa mediante su identificador único.
     *
     * <p>
     * Verifica que la ruta exista, que se encuentre activa y que pertenezca a
     * la empresa indicada antes de convertirla en su DTO.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que debe pertenecer la ruta.
     * @param routeUuid   UUID de la ruta que se desea obtener.
     *
     * @return {@link RouteDTO} correspondiente a la ruta encontrada.
     *
     * @throws NotFoundException  Si la ruta no existe o se encuentra inactiva.
     * @throws ForbiddenException Si la ruta no pertenece a la empresa indicada.
     * @see RouteRepository#findByUuidAndActiveTrue(UUID)
     * @see RouteMapper#toDTO(Route)
     */
    public RouteDTO getRoute(UUID companyUuid, UUID routeUuid) {
        Route route = routeRepository.findByUuidAndActiveTrue(routeUuid)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        if (!route.getCompany().getUuid().equals(companyUuid)) {
            throw new ForbiddenException("No tienes permisos para acceder a esta ruta");
        }
        return routeMapper.toDTO(route);
    }

    /**
     * Obtiene las rutas activas de una empresa.
     *
     * @param companyUuid UUID de la empresa de la que se desean obtener las rutas.
     *
     * @return Lista de {@link RouteDTO} correspondiente a las rutas activas de la
     * empresa.
     *
     * @throws NotFoundException Si la empresa no posee rutas activas.
     * @see RouteRepository#findAllByCompanyUuidAndActiveTrue(UUID)
     * @see RouteMapper#toDTOList(List)
     */
    public List<RouteDTO> getRoutes(UUID companyUuid) {
        List<Route> routes = routeRepository.findAllByCompanyUuidAndActiveTrue(companyUuid);
        if (routes.isEmpty()) {
            throw new NotFoundException("La empresa no posee rutas.");
        }
        return routeMapper.toDTOList(routes);
    }

    /**
     * Actualiza el nombre y el tipo de precio de una ruta de una empresa.
     *
     * <p>
     * La ruta debe estar activa y pertenecer a la empresa indicada. Los puntos
     * y los precios de la ruta no se modifican en esta operación.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece la ruta.
     * @param routeUuid   UUID de la ruta que se desea actualizar.
     * @param routeUpdate Nuevos datos de la ruta ({@link RouteCreate}).
     *
     * @return {@link RouteDTO} correspondiente a la ruta actualizada.
     *
     * @throws NotFoundException Si la ruta no existe, está inactiva o no
     *                           pertenece a la empresa indicada.
     * @see RouteRepository#findByUuidAndCompanyUuidAndActiveTrue(UUID, UUID)
     * @see RouteMapper#toDTO(Route)
     */
    public RouteDTO updateRoute(UUID companyUuid, UUID routeUuid, RouteCreate routeUpdate) {
        Route route = routeRepository.findByUuidAndCompanyUuidAndActiveTrue(routeUuid, companyUuid)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));
        route.setName(routeUpdate.getName());
        route.setPricingType(routeUpdate.getPricingType());
        return routeMapper.toDTO(routeRepository.save(route));
    }

}
