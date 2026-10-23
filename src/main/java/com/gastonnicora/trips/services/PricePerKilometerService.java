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

import java.util.Optional;
import java.util.UUID;

/**
 * Servicio encargado de gestionar las configuraciones de precio por kilómetro.
 *
 * <p>
 * Permite crear la configuración de precio por kilómetro de una ruta a partir
 * de un precio base y un precio por kilómetro adicional.
 * </p>
 *
 * @author Gastón
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
     * Busca la ruta activa indicada dentro de la empresa activa indicada y le
     * asigna la configuración de precio recibida. Si la ruta ya tiene una
     * configuración de precio por kilómetro activa, la operación se rechaza.
     * </p>
     *
     * @param companyUuid             Uuid de la empresa a la que pertenece la ruta.
     * @param routeUuid               Uuid de la ruta a la que se asignará el precio.
     * @param pricePerKilometerCreate Datos del precio por kilómetro que se desea crear
     *                                ({@link PricePerKilometerCreate}).
     *
     * @return {@link PricePerKilometerDTO} correspondiente al precio creado.
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

        Optional<PricePerKilometer> price = pricePerKilometerRepository
                .findByRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrueAndActiveTrue(
                        companyUuid, routeUuid);
        if (price.isPresent()) {
            throw new ConflictException("El precio por kilómetro ya existe en el sistema");
        }

        PricePerKilometer pricePerKilometer = new PricePerKilometer(
                route,
                pricePerKilometerCreate.getBaseKm(),
                pricePerKilometerCreate.getBasePrice(),
                pricePerKilometerCreate.getPricePerKm()
        );

        route.setPricePerKilometer(pricePerKilometer);
        pricePerKilometer = pricePerKilometerRepository.save(pricePerKilometer);
        routeRepository.save(route);

        return pricePerKilometerMapper.toDTO(pricePerKilometer);
    }

    /**
     * Obtiene el precio por kilómetro de una ruta activa de una empresa activa.
     *
     * <p>
     * La búsqueda devuelve el precio solo si el precio, la ruta y la empresa se
     * encuentran activos, y si la ruta pertenece a la empresa indicada.
     * </p>
     *
     * @param companyUuid            Identificador único de la empresa a la que pertenece la ruta.
     * @param routeUuid              Identificador único de la ruta a la que pertenece el precio.
     * @param pricePerKilometerUuid  Identificador único del precio por kilómetro.
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
}