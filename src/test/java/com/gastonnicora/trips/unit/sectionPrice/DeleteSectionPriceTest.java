package com.gastonnicora.trips.unit.sectionPrice;

import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.SectionPriceMapper;
import com.gastonnicora.trips.repositories.RouteRepository;
import com.gastonnicora.trips.repositories.SectionPriceRepository;
import com.gastonnicora.trips.services.SectionPriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSectionPriceTest {

    @InjectMocks
    private SectionPriceService sectionPriceService;

    @Mock
    private SectionPriceRepository sectionPriceRepository;

    @Mock
    private SectionPriceMapper sectionPriceMapper;

    @Mock
    private RouteRepository routeRepository;

    private Company company;
    private Route route;

    private void givenRouteExists() {
        company = new Company();
        company.setUuid(UUID.randomUUID());

        route = new Route(company, "Test route", PricingType.PER_SECTION);
        route.setUuid(UUID.randomUUID());
    }

    private RoutePoint givenPoint(String name) {
        RoutePoint point = new RoutePoint(name, -34.92, -57.95);
        point.setUuid(UUID.randomUUID());
        return point;
    }

    /**
     * Agrega un tramo a la ruta, con el estado indicado. Los tramos se agregan en
     * el orden en que forman la cadena.
     */
    private SectionPrice givenSection(RoutePoint from, RoutePoint to, boolean active) {
        SectionPrice section = new SectionPrice(route, from, to, BigDecimal.valueOf(1000));
        section.setUuid(UUID.randomUUID());
        section.setActive(active);
        route.addSectionPrice(section);
        return section;
    }

    private void givenSectionFound(SectionPrice section) {
        // El repositorio recibe (uuid, companyUuid, routeUuid): el orden debe coincidir
        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             section.getUuid(), company.getUuid(), route.getUuid()))
                .thenReturn(Optional.of(section));
    }

    @Test
    void shouldDeactivateLastActiveSection() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        SectionPrice ab = givenSection(a, b, true);
        SectionPrice bc = givenSection(b, c, true);
        givenSectionFound(bc);

        sectionPriceService.deleteSectionPrice(company.getUuid(), route.getUuid(), bc.getUuid());

        ArgumentCaptor<SectionPrice> captor = ArgumentCaptor.forClass(SectionPrice.class);
        verify(sectionPriceRepository).save(captor.capture());

        // Se da de baja de forma lógica: la misma instancia queda inactiva
        assertSame(bc, captor.getValue());
        assertFalse(bc.isActive());
        assertTrue(ab.isActive());
    }

    @Test
    void shouldDeactivateOnlySectionOfRoute() {

        givenRouteExists();
        SectionPrice ab = givenSection(givenPoint("A"), givenPoint("B"), true);
        givenSectionFound(ab);

        sectionPriceService.deleteSectionPrice(company.getUuid(), route.getUuid(), ab.getUuid());

        assertFalse(ab.isActive());
        verify(sectionPriceRepository).save(ab);
    }

    @Test
    void shouldIgnoreInactiveSectionsAfterLastActiveOne() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        SectionPrice ab = givenSection(a, b, true);
        givenSection(b, c, false);
        givenSectionFound(ab);

        sectionPriceService.deleteSectionPrice(company.getUuid(), route.getUuid(), ab.getUuid());

        assertFalse(ab.isActive());
        verify(sectionPriceRepository).save(ab);
    }

    @Test
    void shouldThrowConflictAndNotSaveWhenSectionIsNotLastActiveOne() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        SectionPrice ab = givenSection(a, b, true);
        givenSection(b, c, true);
        givenSectionFound(ab);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> sectionPriceService.deleteSectionPrice(company.getUuid(), route.getUuid(), ab.getUuid())
                                                  );

        assertEquals("Solo se puede dar de baja el último tramo de la ruta", exception.getMessage());
        assertTrue(ab.isActive());
        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenSectionDoesNotExist() {

        givenRouteExists();
        UUID sectionUuid = UUID.randomUUID();

        when(sectionPriceRepository
                     .findByUuidAndActiveTrueAndRouteCompanyUuidAndRouteCompanyActiveTrueAndRouteUuidAndRouteActiveTrue(
                             sectionUuid, company.getUuid(), route.getUuid()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.deleteSectionPrice(company.getUuid(), route.getUuid(), sectionUuid)
                                                  );

        assertEquals("Tramo no encontrado", exception.getMessage());
        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
    }
}