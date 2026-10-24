package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.TripMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.TripRepository;
import com.gastonnicora.trips.repositories.VehicleRepository;
import com.gastonnicora.trips.repositories.WorkerRepository;
import com.gastonnicora.trips.utils.GeoUtils;
import com.gastonnicora.trips.utils.GeoUtils.Coordinate;
import com.gastonnicora.trips.utils.GeoUtils.RouteMatch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private final WorkerRepository workerRepository;
    private final TripMapper tripMapper;


    /**
     * Crea una instancia del servicio de viajes.
     *
     * @param routeRepository   repositorio de rutas ({@link RouteRepository}).
     * @param tripRepository    repositorio de viajes ({@link TripRepository}).
     * @param vehicleRepository repositorio de vehículos ({@link VehicleRepository}).
     * @param workerRepository  repositorio de trabajadores ({@link WorkerRepository}).
     * @param tripMapper        mapper de viajes ({@link TripMapper}).
     */
    public TripsService(
            RouteRepository routeRepository,
            TripRepository tripRepository,
            VehicleRepository vehicleRepository,
            WorkerRepository workerRepository,
            TripMapper tripMapper
                       ) {
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.workerRepository = workerRepository;
        this.tripMapper = tripMapper;
    }

    /**
     * Crea un viaje programado dentro de una ruta de una empresa.
     *
     * <p>
     * Verifica que la ruta y el vehículo existan dentro de la empresa, y que el
     * conductor sea un trabajador activo con el rol {@link RoleCompany#DRIVER}. El
     * conductor del viaje se toma del trabajador encontrado. Si todas las
     * validaciones pasan, persiste el viaje como activo.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que pertenece el viaje.
     * @param tripCreate  Datos del viaje que se desea crear ({@link TripCreate}).
     *
     * @return {@link TripDTO} correspondiente al viaje creado.
     *
     * @throws BadRequestException Si la ruta o el vehículo no existen dentro de la
     *                             empresa, si el conductor no es un trabajador activo
     *                             de la empresa, o si el trabajador no tiene el rol de
     *                             conductor.
     * @see RouteRepository#findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(UUID, UUID)
     * @see VehicleRepository#findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(UUID, UUID)
     * @see WorkerRepository#findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(UUID, UUID)
     * @see TripMapper#toDTO(Trip)
     */
    @Transactional
    public TripDTO createTrip(UUID companyUuid, TripCreate tripCreate) {
        Route route = routeRepository
                .findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(tripCreate.getRouteUuid(), companyUuid)
                .orElseThrow(
                        () -> new BadRequestException("La ruta solicitada no existe"));
        Vehicle vehicle = vehicleRepository
                .findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(tripCreate.getVehicleUuid(), companyUuid)
                .orElseThrow(
                        () -> new BadRequestException("El vehiculo solicitado no existe"));
        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                        tripCreate.getDriverUuid(), companyUuid).orElseThrow(
                        () -> new BadRequestException("El empleado solicitado no existe"));
        if (!worker.getRoles().contains(RoleCompany.DRIVER)) {
            throw new BadRequestException("El empleado solicitado no existe");
        }
        Trip trip = new Trip(
                route, tripCreate.getDate(), vehicle, worker.getUser(), tripCreate.getType(),
                tripCreate.getDistanceToRoute()
        );
        return tripMapper.toDTO(tripRepository.save(trip));
    }

    /**
     * Obtiene un viaje activo por su identificador único.
     *
     * @param tripUuid Identificador único del viaje.
     *
     * @return {@link TripDTO} correspondiente al viaje encontrado.
     *
     * @throws NotFoundException Si el viaje no existe o está inactivo.
     * @see TripRepository#findByUuidAndActiveTrue(UUID)
     * @see TripMapper#toDTO(Trip)
     */
    @Transactional
    public TripDTO getTrip(UUID tripUuid) {
        Trip trip = tripRepository
                .findByUuidAndActiveTrue(tripUuid).orElseThrow(
                        () -> new NotFoundException("El viaje no existe"));
        return  tripMapper.toDTO(trip);
    }

    /**
     * Obtiene los viajes activos de un día que pasan cerca de un origen y luego
     * cerca de un destino.
     *
     * <p>
     * Un viaje coincide cuando su recorrido pasa a una distancia menor o igual a
     * {@code maxDistanceMeters} tanto de la coordenada de origen como de la de
     * destino, y el punto más cercano al origen aparece antes que el más cercano
     * al destino a lo largo de la ruta, es decir, se recorre en ese sentido.
     * Solo se consideran los viajes cuya fecha de salida cae dentro del día
     * indicado.
     * </p>
     *
     * @param origin            Coordenada de origen (latitud y longitud).
     * @param destination       Coordenada de destino (latitud y longitud).
     * @param maxDistanceMeters Distancia máxima, en metros, entre cada coordenada y
     *                          el recorrido. Debe ser mayor a cero.
     * @param date              Día de salida de los viajes.
     *
     * @return lista de {@link TripDTO} de los viajes que cumplen las condiciones;
     * vacía si no hay coincidencias.
     *
     * @throws BadRequestException Si la distancia máxima no es mayor a cero.
     * @see TripRepository#findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(LocalDateTime, LocalDateTime)
     * @see GeoUtils#closestMatch(Coordinate, List)
     */
    @Transactional
    public List<TripDTO> getTripsPassingBetween(
            Coordinate origin, Coordinate destination, double maxDistanceMeters, LocalDate date) {
        if (maxDistanceMeters <= 0) {
            throw new BadRequestException("La distancia debe ser mayor a cero");
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return tripRepository
                .findAllByActiveTrueAndRouteActiveTrueAndRouteCompanyActiveTrueAndDateGreaterThanEqualAndDateLessThan(
                        start, end)
                .stream()
                .filter(trip -> passesBetween(trip, origin, destination, maxDistanceMeters))
                .map(tripMapper::toDTO)
                .toList();
    }

    /**
     * Indica si el recorrido de un viaje pasa cerca del origen y luego cerca del
     * destino, dentro de la distancia máxima indicada.
     */
    private boolean passesBetween(Trip trip, Coordinate origin, Coordinate destination, double maxDistanceMeters) {
        List<Coordinate> polyline = trip.getRoute().getPoints().stream()
                .filter(RoutePoint::isActive)
                .map(point -> new Coordinate(point.getLatitude(), point.getLongitude()))
                .toList();
        if (polyline.isEmpty()) {
            return false;
        }

        RouteMatch originMatch = GeoUtils.closestMatch(origin, polyline);
        RouteMatch destinationMatch = GeoUtils.closestMatch(destination, polyline);

        return originMatch.distanceMeters() <= maxDistanceMeters
                && destinationMatch.distanceMeters() <= maxDistanceMeters
                && originMatch.progress() < destinationMatch.progress();
    }

    public TripDTO updateTrip(UUID companyUuid,UUID tripUuid, TripCreate tripUpdate) {
        Trip trip= tripRepository.findByUuidAndRouteCompanyUuidAndActiveTrueAndRouteCompanyActiveTrue(tripUuid,companyUuid).orElseThrow(
                () -> new NotFoundException("El viaje solicitado no existe"));
        Route route = routeRepository
                .findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(tripUpdate.getRouteUuid(), companyUuid)
                .orElseThrow(
                        () -> new BadRequestException("La ruta solicitada no existe"));
        Vehicle vehicle = vehicleRepository
                .findByUuidAndActiveTrueAndCompanyUuidAndCompanyActiveTrue(tripUpdate.getVehicleUuid(), companyUuid)
                .orElseThrow(
                        () -> new BadRequestException("El vehiculo solicitado no existe"));
        Worker worker = workerRepository
                .findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                        tripUpdate.getDriverUuid(), companyUuid).orElseThrow(
                        () -> new BadRequestException("El empleado solicitado no existe"));
        if (!worker.getRoles().contains(RoleCompany.DRIVER)) {
            throw new BadRequestException("El empleado solicitado no existe");
        }
        trip.setRoute(route);
        Trip trip = new Trip(
                route, tripUpdate.getDate(), vehicle, worker.getUser(), tripUpdate.getType(),
                tripUpdate.getDistanceToRoute()
        );
        return tripMapper.toDTO(tripRepository.save(trip));
    }
}