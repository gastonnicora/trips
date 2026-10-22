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

import java.util.List;
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
 * @version 1.0
 * @since 2026-10-19
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
     * @param routeRepository      repositorio utilizado para acceder a las rutas
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

    /**
     * Obtiene un punto de ruta activo que pertenezca a una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda delega en
     * {@link RoutePointRepository#findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(UUID, UUID, UUID)}.
     * El punto solo se devuelve si el punto, su ruta y la empresa a la que pertenece
     * la ruta están activos, y si la ruta pertenece a la empresa indicada.
     * </p>
     *
     * @param companyUuid    Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid      Identificador único de la ruta a la que pertenece el punto.
     * @param routePointUuid Identificador único del punto de ruta.
     *
     * @return {@link RoutePointDTO} correspondiente al punto de ruta encontrado.
     *
     * @throws NotFoundException Si no existe un punto de ruta activo que cumpla con las
     *                           condiciones indicadas.
     * @see RoutePointRepository#findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(UUID, UUID, UUID)
     * @see RoutePointMapper#toDTO(RoutePoint)
     */
    public RoutePointDTO getRoutePoint(UUID companyUuid, UUID routeUuid, UUID routePointUuid) {
        RoutePoint routePoint = routePointRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                        companyUuid, routeUuid, routePointUuid)
                .orElseThrow(() -> new NotFoundException("No existe el punto de ruta."));
        return routePointMapper.toDTO(routePoint);
    }


    /**
     * Obtiene todos los puntos de ruta activos de una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda delega en
     * {@link RoutePointRepository#findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(UUID, UUID)}.
     * Solo se devuelven los puntos cuya ruta pertenece a la empresa indicada y está activa,
     * y cuya empresa también está activa.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta cuyos puntos se desean obtener.
     *
     * @return lista de {@link RoutePointDTO} con los puntos activos de la ruta.
     *
     * @throws NotFoundException Si la ruta no tiene puntos activos, o si la ruta no existe
     *                           o no pertenece a la empresa indicada.
     * @see RoutePointRepository#findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(UUID, UUID)
     * @see RoutePointMapper#toDTOList(List)
     */
    public List<RoutePointDTO> getRoutePoints(UUID companyUuid, UUID routeUuid) {
        List<RoutePoint> routePoints = routePointRepository.findAllByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                companyUuid, routeUuid);
        if (routePoints.isEmpty()) {
            throw new NotFoundException("No existen puntos de rutas para la ruta.");
        }
        return routePointMapper.toDTOList(routePoints);
    }

    /**
     * Actualiza el nombre y las coordenadas de un punto de ruta activo.
     *
     * <p>
     * El punto se busca con
     * {@link RoutePointRepository#findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(UUID, UUID, UUID)},
     * por lo que solo se actualizan puntos activos, cuya ruta esté activa, que pertenezca
     * a la empresa indicada y cuya empresa también esté activa.
     * </p>
     *
     * @param companyUuid      Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid        Identificador único de la ruta a la que pertenece el punto.
     * @param routePointUuid   Identificador único del punto de ruta que se desea actualizar.
     * @param routePointUpdate Nuevos datos del punto de ruta ({@link RoutePointCreate}).
     *
     * @return {@link RoutePointDTO} correspondiente al punto de ruta actualizado.
     *
     * @throws NotFoundException Si no existe un punto de ruta activo que cumpla con las
     *                           condiciones indicadas.
     * @see RoutePointRepository#findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(UUID, UUID, UUID)
     * @see RoutePointMapper#toDTO(RoutePoint)
     */
    public RoutePointDTO updateRoutePoint(
            UUID companyUuid, UUID routeUuid, UUID routePointUuid, RoutePointCreate routePointUpdate) {
        RoutePoint routePoint = routePointRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndUuidAndActiveTrue(
                        companyUuid, routeUuid, routePointUuid)
                .orElseThrow(() -> new NotFoundException("No existe el punto de ruta."));
        routePoint.setLatitude(routePointUpdate.getLatitude());
        routePoint.setLongitude(routePointUpdate.getLongitude());
        routePoint.setName(routePointUpdate.getName());
        routePoint = routePointRepository.save(routePoint);
        return routePointMapper.toDTO(routePoint);
    }
}