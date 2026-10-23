package com.gastonnicora.trips.unit.sectionPrice;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.SectionPriceMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.SectionPriceRepository;
import com.gastonnicora.trips.services.SectionPriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSectionPricesTest {

    @InjectMocks
    private SectionPriceService sectionPriceService;

    @Mock
    private SectionPriceRepository sectionPriceRepository;

    @Mock
    private SectionPriceMapper sectionPriceMapper;

    @Mock
    private RouteRepository routeRepository;

    private UUID companyUuid;
    private UUID routeUuid;

    private void givenIdentifiers() {
        companyUuid = UUID.randomUUID();
        routeUuid = UUID.randomUUID();
    }

    private SectionPrice givenSectionPrice() {
        SectionPrice sectionPrice = new SectionPrice(null, null, null, BigDecimal.valueOf(1000));
        sectionPrice.setUuid(UUID.randomUUID());
        sectionPrice.setActive(true);
        return sectionPrice;
    }

    @Test
    void shouldReturnSectionPricesWhenRouteHasActiveSections() {

        givenIdentifiers();
        List<SectionPrice> sections = List.of(givenSectionPrice(), givenSectionPrice());
        List<SectionPriceDTO> expectedDTOs = List.of(new SectionPriceDTO(), new SectionPriceDTO());

        // El repositorio recibe (companyUuid, routeUuid)
        when(sectionPriceRepository
                     .findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             companyUuid, routeUuid))
                .thenReturn(sections);
        when(sectionPriceMapper.toDTOList(sections)).thenReturn(expectedDTOs);

        List<SectionPriceDTO> result = sectionPriceService.getSectionPrices(companyUuid, routeUuid);

        assertSame(expectedDTOs, result);
        verify(sectionPriceMapper).toDTOList(sections);
    }

    @Test
    void shouldThrowNotFoundWhenRouteHasNoActiveSections() {

        givenIdentifiers();

        when(sectionPriceRepository
                     .findByActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             companyUuid, routeUuid))
                .thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.getSectionPrices(companyUuid, routeUuid)
                                                  );

        assertEquals("Tramo no encontrado", exception.getMessage());
        verify(sectionPriceMapper, never()).toDTOList(anyList());
    }
}