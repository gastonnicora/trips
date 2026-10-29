package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.PassageDTO;
import com.gastonnicora.trips.dtos.request.passage.PassageCreate;
import com.gastonnicora.trips.entities.Passage;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.enums.TypeTrip;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.PassageMapper;
import com.gastonnicora.trips.repositories.PassageRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.utils.GeoUtils;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
import com.gastonnicora.trips.utils.GeoUtils.RouteMatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Servicio encargado de gestionar los pasajes de un viaje.
 *
 * <p>
 * Valida que el punto de subida y el de bajada estén sobre el recorrido por
 * caminos reales del viaje y calcula el precio según la forma de cobro de la
 * ruta: por kilómetro recorrido ({@link PricingType#PER_KM}) o por suma de tramos
 * con precio fijo ({@link PricingType#PER_SECTION}). El pasaje se persiste como
 * activo.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Service
public class PassageService {

    /**
     * Distancia máxima, en metros, entre un punto y el recorrido de un viaje de
     * tipo {@link TypeTrip#TOUR}.
     */
    private static final double TOUR_TOLERANCE_METERS = 100.0;

    /**
     * Margen para comparar posiciones sobre el recorrido con errores de redondeo.
     */
    private static final double EPSILON = 1e-9;

    private final PassageMapper passageMapper;
    private final PassageRepository passageRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final RoutingService routingService;

    /**
     * Crea una instancia del servicio de pasajes.
     *
     * @param passageMapper     mapper de pasajes ({@link PassageMapper}).
     * @param passageRepository repositorio de pasajes ({@link PassageRepository}).
     * @param tripRepository    repositorio de viajes ({@link TripRepository}).
     * @param userRepository    repositorio de usuarios ({@link UserRepository}).
     * @param routingService    servicio que calcula el recorrido por caminos reales
     *                          ({@link RoutingService}).
     */
    public PassageService(
            PassageMapper passageMapper,
            PassageRepository passageRepository,
            TripRepository tripRepository,
            UserRepository userRepository,
            RoutingService routingService
                         ) {
        this.passageMapper = passageMapper;
        this.passageRepository = passageRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.routingService = routingService;
    }

    /**
     * Crea un pasaje para un usuario dentro de un viaje activo.
     *
     * <p>
     * Los puntos de subida y bajada se proyectan sobre el recorrido por caminos
     * reales del viaje. Para un viaje {@link TypeTrip#TOUR} deben estar sobre el
     * recorrido, con una tolerancia de {@link #TOUR_TOLERANCE_METERS}. Para un viaje
     * {@link TypeTrip#DOMICILE} pueden estar desviados hasta
     * {@code distanceToRoute} metros. La subida debe ir antes que la bajada en el
     * sentido del recorrido. El precio se calcula según el {@link PricingType} de la
     * ruta.
     * </p>
     *
     * @param userUuid      identificador del usuario que reserva el pasaje.
     * @param passageCreate datos del pasaje ({@link PassageCreate}).
     *
     * @return {@link PassageDTO} correspondiente al pasaje creado.
     *
     * @throws NotFoundException   Si el viaje, la ruta, la empresa o el usuario no
     *                             existen o están inactivos.
     * @throws ConflictException   Si la ruta no tiene recorrido válido, si el viaje
     *                             a domicilio no tiene distancia de desvío, o si la
     *                             ruta no tiene la configuración de precio que
     *                             necesita su forma de cobro.
     * @throws BadRequestException Si la subida o la bajada están fuera del recorrido,
     *                             o si la bajada no está después de la subida.
     */
    @Transactional
    public PassageDTO createPassage(UUID userUuid, PassageCreate passageCreate) {
        Trip trip = tripRepository.findByUuidAndActiveTrue(passageCreate.getTripUuid())
                .orElseThrow(() -> new NotFoundException("El viaje solicitado no existe"));

        Route route = trip.getRoute();
        if (!route.isActive() || !route.getCompany().isActive()) {
            throw new NotFoundException("El viaje solicitado no existe");
        }

        User user = userRepository.findByUuidAndEnabledTrue(userUuid)
                .orElseThrow(() -> new NotFoundException("El usuario solicitado no existe"));

        List<Coordinate> road = getRoad(route);

        Coordinate from = new Coordinate(passageCreate.getFromLatitude(), passageCreate.getFromLongitude());
        Coordinate to = new Coordinate(passageCreate.getToLatitude(), passageCreate.getToLongitude());

        RouteMatch fromMatch = GeoUtils.closestMatch(from, road);
        RouteMatch toMatch = GeoUtils.closestMatch(to, road);

        validateOnRoute(trip, fromMatch, toMatch);

        BigDecimal price = switch (route.getPricingType()) {
            case PER_KM -> calculatePricePerKm(route, road, fromMatch, toMatch);
            case PER_SECTION -> calculatePriceBySections(route, road, fromMatch, toMatch);
        };

        Passage passage = new Passage(
                trip,
                user,
                price,
                passageCreate.getFromLatitude(),
                passageCreate.getFromLongitude(),
                passageCreate.getToLatitude(),
                passageCreate.getToLongitude()
        );

        return passageMapper.toDTO(passageRepository.save(passage));
    }

    /**
     * Obtiene el recorrido por caminos reales de la ruta, a partir de sus puntos
     * activos.
     *
     * @param route ruta cuyo recorrido se necesita.
     *
     * @return geometría del recorrido, en orden.
     *
     * @throws ConflictException Si la ruta no tiene al menos dos puntos activos.
     */
    private List<Coordinate> getRoad(Route route) {
        List<Coordinate> waypoints = route.getPoints().stream()
                .filter(RoutePoint::isActive)
                .map(point -> new Coordinate(point.getLatitude(), point.getLongitude()))
                .toList();
        if (waypoints.size() < 2) {
            throw new ConflictException("La ruta del viaje no tiene un recorrido válido");
        }
        return routingService.getRoadPath(route.getUuid(), waypoints).geometry();
    }

    /**
     * Verifica que la subida y la bajada estén dentro de la tolerancia del viaje y
     * que la subida vaya antes que la bajada en el sentido del recorrido.
     *
     * <p>
     * La tolerancia es {@link #TOUR_TOLERANCE_METERS} para un viaje
     * {@link TypeTrip#TOUR}, y {@code distanceToRoute} para un viaje
     * {@link TypeTrip#DOMICILE}.
     * </p>
     *
     * @param trip      viaje al que pertenece el pasaje.
     * @param fromMatch posición de la subida sobre el recorrido.
     * @param toMatch   posición de la bajada sobre el recorrido.
     *
     * @throws ConflictException   Si el viaje a domicilio no tiene distancia de
     *                             desvío configurada.
     * @throws BadRequestException Si algún punto está fuera de la tolerancia o si
     *                             la bajada no está después de la subida.
     */
    private void validateOnRoute(Trip trip, RouteMatch fromMatch, RouteMatch toMatch) {
        double tolerance;
        if (trip.getType() == TypeTrip.DOMICILE) {
            if (trip.getDistanceToRoute() == null) {
                throw new ConflictException("El viaje a domicilio no tiene distancia de desvío configurada");
            }
            tolerance = trip.getDistanceToRoute();
        } else {
            tolerance = TOUR_TOLERANCE_METERS;
        }

        if (fromMatch.distanceMeters() > tolerance || toMatch.distanceMeters() > tolerance) {
            throw new BadRequestException("El punto de subida o de bajada está fuera del recorrido");
        }
        if (fromMatch.progress() >= toMatch.progress()) {
            throw new BadRequestException("La bajada debe estar después de la subida");
        }
    }

    /**
     * Calcula el precio de un pasaje según los kilómetros recorridos entre la
     * subida y la bajada. El precio base cubre hasta {@code baseKm} kilómetros y,
     * a partir de ahí, se cobra {@code pricePerKm} por cada kilómetro adicional.
     *
     * @param route     ruta del viaje, con su configuración de precio por kilómetro.
     * @param road      recorrido por caminos reales de la ruta.
     * @param fromMatch posición de la subida sobre el recorrido.
     * @param toMatch   posición de la bajada sobre el recorrido.
     *
     * @return precio del pasaje, redondeado a dos decimales.
     *
     * @throws ConflictException Si la ruta no tiene precio por kilómetro activo.
     */
    private BigDecimal calculatePricePerKm(
            Route route, List<Coordinate> road, RouteMatch fromMatch, RouteMatch toMatch) {

        PricePerKilometer pricePerKilometer = route.getPricePerKilometer();
        if (pricePerKilometer == null || !pricePerKilometer.isActive()) {
            throw new ConflictException("La ruta no tiene precio por kilómetro");
        }

        double meters = GeoUtils.distanceAlongMeters(road, toMatch.progress())
                - GeoUtils.distanceAlongMeters(road, fromMatch.progress());
        BigDecimal kilometers = BigDecimal.valueOf(meters / 1000.0);

        BigDecimal price = pricePerKilometer.getBasePrice();
        if (kilometers.compareTo(pricePerKilometer.getBaseKm()) > 0) {
            BigDecimal extraKm = kilometers.subtract(pricePerKilometer.getBaseKm());
            price = price.add(extraKm.multiply(pricePerKilometer.getPricePerKm()));
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el precio de un pasaje sumando los tramos activos de la ruta que el
     * pasajero recorre entre la subida y la bajada. Un tramo se cobra completo si
     * se superpone con el recorrido del pasajero.
     *
     * <p>
     * Se consideran solo los tramos cuyos dos puntos están activos. Los tramos
     * cubiertos deben formar una cadena continua que llegue desde la subida hasta
     * la bajada; si falta alguno, se rechaza el pasaje.
     * </p>
     *
     * @param route     ruta del viaje, con sus tramos.
     * @param road      recorrido por caminos reales de la ruta.
     * @param fromMatch posición de la subida sobre el recorrido.
     * @param toMatch   posición de la bajada sobre el recorrido.
     *
     * @return precio del pasaje, redondeado a dos decimales.
     *
     * @throws ConflictException Si el pasaje no está completamente cubierto por
     *                           tramos activos con precio.
     */
    private BigDecimal calculatePriceBySections(
            Route route, List<Coordinate> road, RouteMatch fromMatch, RouteMatch toMatch) {

        double from = fromMatch.progress();
        double to = toMatch.progress();

        List<Span> covering = route.getSectionPrices().stream()
                .filter(SectionPrice::isActive)
                .filter(section -> section.getFromPoint().isActive() && section.getToPoint().isActive())
                .map(section -> new Span(
                        section,
                        progressOf(section.getFromPoint(), road),
                        progressOf(section.getToPoint(), road)))
                .sorted(Comparator.comparingDouble(Span::start))
                .filter(span -> span.start() < to && span.end() > from)
                .toList();

        if (covering.isEmpty()) {
            throw new ConflictException("No hay precio cargado para este tramo");
        }

        if (covering.getFirst().start() > from + EPSILON || covering.getLast().end() < to - EPSILON) {
            throw new ConflictException("No hay precio cargado para todo el tramo del pasaje");
        }

        for (int i = 1; i < covering.size(); i++) {
            if (covering.get(i).start() > covering.get(i - 1).end() + EPSILON) {
                throw new ConflictException("No hay precio cargado para todo el tramo del pasaje");
            }
        }

        return covering.stream()
                .map(span -> span.section().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Posición de un punto de la ruta sobre el recorrido, en la misma escala que
     * {@link RouteMatch#progress()}.
     *
     * @param point punto de la ruta.
     * @param road  recorrido por caminos reales de la ruta.
     *
     * @return posición del punto sobre el recorrido.
     */
    private double progressOf(RoutePoint point, List<Coordinate> road) {
        Coordinate coordinate = new Coordinate(point.getLatitude(), point.getLongitude());
        return GeoUtils.closestMatch(coordinate, road).progress();
    }

    /**
     * Tramo de la ruta junto con su posición de inicio y fin sobre el recorrido.
     *
     * @param section tramo con precio.
     * @param start   posición del punto de inicio del tramo sobre el recorrido.
     * @param end     posición del punto de fin del tramo sobre el recorrido.
     */
    private record Span(SectionPrice section, double start, double end) {
    }
}