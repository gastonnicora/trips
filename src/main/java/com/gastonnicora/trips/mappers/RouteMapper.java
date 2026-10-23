package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.entities.Route;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente encargado de convertir entidades {@link Route} en objetos
 * {@link RouteDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes, incluyendo la empresa, los puntos,
 * el precio por kilómetro y los precios por tramo de la ruta.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-20
 */
@Component
public class RouteMapper {

    private final CompanyMapper companyMapper;
    private final RoutePointMapper routePointMapper;
    private final PricePerKilometerMapper pricePerKilometerMapper;
    private final SectionPriceMapper sectionPriceMapper;

    /**
     * Crea una instancia del mapper de rutas.
     *
     * @param companyMapper           Mapper ({@link CompanyMapper}) utilizado para
     *                                convertir la empresa asociada a la ruta.
     * @param routePointMapper        Mapper ({@link RoutePointMapper}) utilizado para
     *                                convertir los puntos de la ruta.
     * @param pricePerKilometerMapper Mapper ({@link PricePerKilometerMapper})
     *                                utilizado para convertir el precio por kilómetro.
     * @param sectionPriceMapper      Mapper ({@link SectionPriceMapper}) utilizado
     *                                para convertir los precios por tramo.
     */
    public RouteMapper(
            CompanyMapper companyMapper, RoutePointMapper routePointMapper,
            PricePerKilometerMapper pricePerKilometerMapper, SectionPriceMapper sectionPriceMapper
                      ) {
        this.companyMapper = companyMapper;
        this.routePointMapper = routePointMapper;
        this.pricePerKilometerMapper = pricePerKilometerMapper;
        this.sectionPriceMapper = sectionPriceMapper;
    }

    /**
     * Convierte una entidad {@link Route} en un {@link RouteDTO}.
     *
     * <p>
     * La conversión incluye los datos de identificación, empresa asociada, nombre,
     * tipo de precio, puntos, precio por kilómetro, precios por tramo, fechas de
     * creación y actualización, y estado.
     * </p>
     *
     * @param route Entidad de ruta que se desea convertir.
     *
     * @return DTO de ruta correspondiente a la entidad proporcionada, o
     * {@code null} si la entidad es {@code null}.
     */
    public RouteDTO toDTO(Route route) {
        if (route == null) {
            return null;
        }
        return new RouteDTO(
                route.getUuid(), companyMapper.toDTO(route.getCompany()),
                route.getName(), route.getPricingType(), routePointMapper.toDTOList(route.getPoints()),
                pricePerKilometerMapper.toDTO(route.getPricePerKilometer()),
                sectionPriceMapper.toDTOList(route.getSectionPrices()),
                route.getCreatedAt(), route.getUpdatedAt(), route.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link Route} en una lista de
     * {@link RouteDTO}.
     *
     * @param routes Lista de entidades de rutas que se desea convertir.
     *
     * @return Lista de DTOs de rutas correspondientes a las entidades
     * proporcionadas.
     */
    public List<RouteDTO> toDTOList(List<Route> routes) {
        return routes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
