package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.PricePerKilometerDTO;
import com.gastonnicora.trips.dtos.request.pricePerKilometer.PricePerKilometerCreate;
import com.gastonnicora.trips.entities.PricePerKilometer;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.PricePerKilometerMapper;
import com.gastonnicora.trips.repositories.PricePerKilometerRepository;
import com.gastonnicora.trips.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio encargado de gestionar las configuraciones de precio por kilómetro.
 *
 * <p>
 * Permite crear, obtener, actualizar y dar de baja la configuración de precio
 * por kilómetro de una ruta a partir de un precio base y un precio por kilómetro
 * adicional.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-22
 */
@Service
public class PricePerKilometerService {

    private final PricePerKilometerRepository pricePerKilometerRepository;
    private final RouteRepository routeRepository;
    private final PricePerKilometerMapper pricePerKilometerMapper;

    /**
     * Crea una instancia del servicio de precios por kilómetro.
     *
     * @param pricePerKilometerRepository Repositorio utilizado para acceder a las
     *                                    configuraciones de precio por kilómetro
     *                                    ({@link PricePerKilometerRepository}).
     * @param routeRepository             Repositorio utilizado para acceder a las
     *                                    rutas ({@link RouteRepository}).
     * @param pricePerKilometerMapper     Mapper utilizado para convertir entidades
     *                                    {@link PricePerKilometer} en
     *                                    {@link PricePerKilometerDTO}
     *                                    ({@link PricePerKilometerMapper}).
     */
    public PricePerKilometerService(
            PricePerKilometerRepository pricePerKilometerRepository,
            RouteRepository routeRepository,
            PricePerKilometerMapper pricePerKilometerMapper
                                   ) {
        this.pricePerKilometerRepository = pricePerKilometerRepository;
        this.routeRepository = routeRepository;
        this.pricePerKilometerMapper = pricePerKilometerMapper;
    }

    /**
     * Crea la configuración de precio por kilómetro de una ruta.
     *
     * <p>
     * Busca la ruta activa indicada dentro de la empresa activa indicada. Si la
     * ruta no tiene ninguna configuración de precio, se crea una nueva. Si ya tiene
     * una configuración inactiva, esa misma configuración se reutiliza: se actualizan
     * sus valores y se vuelve a activar, ya que la ruta solo puede tener una
     * configuración de precio por kilómetro.
     * </p>
     *
     * <p>
     * Si la ruta ya tiene una configuración activa, la operación se rechaza.
     * </p>
     *
     * @param companyUuid             Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid               Identificador único de la ruta a la que se asignará el precio.
     * @param pricePerKilometerCreate Datos del precio por kilómetro que se desea crear
     *                                ({@link PricePerKilometerCreate}).
     *
     * @return {@link PricePerKilometerDTO} correspondiente al precio creado o reactivado.
     *
     * @throws NotFoundException Si la ruta no existe, se encuentra inactiva o no
     *                           pertenece a la empresa indicada.
     * @throws ConflictException Si la ruta ya tiene una configuración de precio por
     *                           kilómetro activa.
     * @see PricePerKilometerMapper#toDTO(PricePerKilometer)
     */
    public PricePerKilometerDTO createPricePerKilometer(
            UUID companyUuid, UUID routeUuid, PricePerKilometerCreate pricePerKilometerCreate) {

        Route route = routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid)
                .orElseThrow(() -> new NotFoundException("Ruta no encontrada"));

        PricePerKilometer pricePerKilometer = route.getPricePerKilometer();

        if (pricePerKilometer != null && pricePerKilometer.isActive()) {
            throw new ConflictException("El precio por kilómetro ya existe en el sistema");
        }

        if (pricePerKilometer == null) {
            pricePerKilometer = new PricePerKilometer(
                    route,
                    pricePerKilometerCreate.getBaseKm(),
                    pricePerKilometerCreate.getBasePrice(),
                    pricePerKilometerCreate.getPricePerKm()
            );
            route.setPricePerKilometer(pricePerKilometer);
        } else {
            pricePerKilometer.setBaseKm(pricePerKilometerCreate.getBaseKm());
            pricePerKilometer.setBasePrice(pricePerKilometerCreate.getBasePrice());
            pricePerKilometer.setPricePerKm(pricePerKilometerCreate.getPricePerKm());
            pricePerKilometer.setActive(true);
        }

        pricePerKilometer = pricePerKilometerRepository.save(pricePerKilometer);
        routeRepository.save(route);
        return pricePerKilometerMapper.toDTO(pricePerKilometer);
    }

    /**
     * Da de baja la configuración de precio por kilómetro de una ruta.
     *
     * <p>
     * La baja es lógica: la configuración no se elimina de la base de datos, sino
     * que se marca como inactiva. Solo se pueden dar de baja precios activos, cuya
     * ruta esté activa, que pertenezca a la empresa indicada y cuya empresa también
     * esté activa.
     * </p>
     *
     * @param companyUuid Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid   Identificador único de la ruta a la que pertenece el precio.
     * @param uuid        Identificador único del precio por kilómetro que se desea dar de baja.
     *
     * @throws NotFoundException Si el precio no existe, está inactivo, o su ruta o
     *                           empresa no cumplen las condiciones indicadas.
     * @see PricePerKilometerRepository#findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(UUID, UUID, UUID)
     */
    public void deletePricePerKilometer(UUID companyUuid, UUID routeUuid, UUID uuid) {
        PricePerKilometer price = pricePerKilometerRepository
                .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        uuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Precio por kilómetro no encontrado"));

        price.setActive(false);
        pricePerKilometerRepository.save(price);
    }

    /**
     * Obtiene el precio por kilómetro de una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda devuelve el precio solo si el precio, la ruta y la empresa se
     * encuentran activos, y si la ruta pertenece a la empresa indicada.
     * </p>
     *
     * @param companyUuid           Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid             Identificador único de la ruta a la que pertenece el precio.
     * @param pricePerKilometerUuid Identificador único del precio por kilómetro.
     *
     * @return {@link PricePerKilometerDTO} correspondiente al precio encontrado.
     *
     * @throws NotFoundException Si el precio no existe, está inactivo, o su ruta o
     *                           empresa no cumplen las condiciones indicadas.
     * @see PricePerKilometerMapper#toDTO(PricePerKilometer)
     */
    public PricePerKilometerDTO getPricePerKilometer(UUID companyUuid, UUID routeUuid, UUID pricePerKilometerUuid) {
        PricePerKilometer price = pricePerKilometerRepository
                .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        pricePerKilometerUuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Precio por kilómetro no encontrado"));

        return pricePerKilometerMapper.toDTO(price);
    }

    /**
     * Actualiza la configuración de precio por kilómetro de una ruta activa.
     *
     * <p>
     * El precio se busca con
     * {@link PricePerKilometerRepository#findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(UUID, UUID, UUID)},
     * por lo que solo se actualizan precios activos, cuya ruta esté activa, que
     * pertenezca a la empresa indicada y cuya empresa también esté activa.
     * </p>
     *
     * @param companyUuid             Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid               Identificador único de la ruta a la que pertenece el precio.
     * @param priceUuid               Identificador único del precio por kilómetro que se desea actualizar.
     * @param pricePerKilometerUpdate Nuevos datos del precio por kilómetro ({@link PricePerKilometerCreate}).
     *
     * @return {@link PricePerKilometerDTO} correspondiente al precio actualizado.
     *
     * @throws NotFoundException Si el precio no existe, está inactivo, o su ruta o
     *                           empresa no cumplen las condiciones indicadas.
     * @see PricePerKilometerMapper#toDTO(PricePerKilometer)
     */
    public PricePerKilometerDTO updatePricePerKilometer(
            UUID companyUuid, UUID routeUuid, UUID priceUuid, PricePerKilometerCreate pricePerKilometerUpdate) {

        PricePerKilometer pricePerKilometer = pricePerKilometerRepository
                .findByUuidAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        priceUuid, companyUuid, routeUuid)
                .orElseThrow(() -> new NotFoundException("Precio por kilómetro no encontrado"));

        pricePerKilometer.setBaseKm(pricePerKilometerUpdate.getBaseKm());
        pricePerKilometer.setBasePrice(pricePerKilometerUpdate.getBasePrice());
        pricePerKilometer.setPricePerKm(pricePerKilometerUpdate.getPricePerKm());

        pricePerKilometer = pricePerKilometerRepository.save(pricePerKilometer);
        return pricePerKilometerMapper.toDTO(pricePerKilometer);
    }
}