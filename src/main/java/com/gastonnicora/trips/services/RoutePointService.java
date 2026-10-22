package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.RoutePointDTO;
import com.gastonnicora.trips.dtos.request.routePoint.RoutePointCreate;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.RoutePointMapper;
import com.gastonnicora.trips.repositories.RoutePointRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio que encapsula la lógica de negocio relacionada con los puntos de ruta
 * ({@code RoutePoint}) de un viaje.
 *
 * <p>Actúa como intermediario entre la capa de acceso a datos
 * ({@link RoutePointRepository}) y la capa de presentación, utilizando
 * {@link RoutePointMapper} para convertir entre entidades y DTOs.</p>
 *
 * @author Gastón Nicora
 * @see RoutePointRepository
 * @see RoutePointMapper
 * @since 1.0
 */
@Service
public class RoutePointService {

    private final RoutePointRepository routePointRepository;

    private final RoutePointMapper routePointMapper;

    private final RouteRepository routeRepository;

    /**
     * Crea una instancia del servicio de puntos de rutas.
     *
     * @param routePointRepository repositorio utilizado para acceder a los puntos de ruta
     * @param routePointMapper     mapper utilizado para convertir entre entidades y DTOs
     */
    public RoutePointService(
            RoutePointRepository routePointRepository, RoutePointMapper routePointMapper,
            RouteRepository routeRepository
                            ) {
        this.routePointRepository = routePointRepository;
        this.routePointMapper = routePointMapper;
        this.routeRepository = routeRepository;
    }

    /**
     * Crea un nuevo punto de ruta asociada a una ruta.
     *
     * <p>
     * El punto de ruta se crea con el nombre y las coordenadas geográficas.
     * </p>
     *
     * @param companyUuid      Identificador unico de la empresa a la que pertenece la ruta.
     * @param routeUuid        Identificador unico de la ruta a la que se asociará el punto de ruta.
     * @param routePointCreate Datos del punto de ruta que se desea crear ({@link RoutePointCreate}).
     *
     * @return {@link RoutePointDTO} correspondiente al punto de ruta creado.
     *
     * @see RouteRepository#findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID, UUID)
     * @see RoutePointMapper#toDTO(RoutePoint)
     */
    @Transactional
    public RoutePointDTO createRoutePoint(UUID companyUuid, UUID routeUuid, RoutePointCreate routePointCreate) {
        Route route = routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));
        RoutePoint routePoint = new RoutePoint(
                routePointCreate.getName(), routePointCreate.getLatitude(), routePointCreate.getLongitude());
        route.addPoint(routePoint);
        return routePointMapper.toDTO(routePointRepository.save(routePoint));
    }
}