package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.TripDTO;
import com.gastonnicora.trips.dtos.request.trip.TripCreate;
import com.gastonnicora.trips.entities.Route;
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
    public TripDTO getTrip(UUID tripUuid) {
        Trip trip = tripRepository
                .findByUuidAndActiveTrue(tripUuid).orElseThrow(
                        () -> new NotFoundException("El viaje no existe"));
        return  tripMapper.toDTO(trip);
    }
}
