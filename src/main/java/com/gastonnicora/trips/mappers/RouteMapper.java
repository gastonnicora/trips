package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.RouteDTO;
import com.gastonnicora.trips.entities.Route;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteMapper {

    private final CompanyMapper companyMapper;
    private final RoutePointMapper routePointMapper;
    private final PricePerKilometerMapper pricePerKilometerMapper;
    private final SectionPriceMapper sectionPriceMapper;

    public RouteMapper(
            CompanyMapper companyMapper, RoutePointMapper routePointMapper,
            PricePerKilometerMapper pricePerKilometerMapper, SectionPriceMapper sectionPriceMapper
                      ) {
        this.companyMapper = companyMapper;
        this.routePointMapper = routePointMapper;
        this.pricePerKilometerMapper = pricePerKilometerMapper;
        this.sectionPriceMapper = sectionPriceMapper;
    }

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

    public List<RouteDTO> toDTOList(List<Route> routes) {
        return routes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
