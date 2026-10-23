package com.gastonnicora.trips.unit.sectionPrice;

import com.gastonnicora.trips.dtos.entities.SectionPriceDTO;
import com.gastonnicora.trips.dtos.request.sectionPrice.SectionPriceCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Route;
import com.gastonnicora.trips.entities.RoutePoint;
import com.gastonnicora.trips.entities.SectionPrice;
import com.gastonnicora.trips.enums.PricingType;
import com.gastonnicora.trips.exceptions.BadRequestException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSectionPriceTest {

    @InjectMocks
    private SectionPriceService sectionPriceService;

    @Mock
    private SectionPriceRepository sectionPriceRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private SectionPriceMapper sectionPriceMapper;

    private Company company;
    private Route route;

    private void givenRouteExists() {
        company = new Company();
        company.setUuid(UUID.randomUUID());

        route = new Route(company, "Test route", PricingType.PER_SECTION);
        route.setUuid(UUID.randomUUID());

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(
                route.getUuid(), company.getUuid())).thenReturn(Optional.of(route));
    }

    /**
     * Agrega un punto activo al final de la ruta. El uuid es necesario porque el
     * servicio compara puntos por identificador.
     */
    private RoutePoint givenPoint(String name) {
        RoutePoint point = new RoutePoint(name, -34.92, -57.95);
        point.setUuid(UUID.randomUUID());
        route.addPoint(point);
        return point;
    }

    /**
     * Agrega un tramo ya existente a la ruta, con el estado indicado.
     */
    private SectionPrice givenSection(RoutePoint from, RoutePoint to, BigDecimal price, boolean active) {
        SectionPrice section = new SectionPrice(route, from, to, price);
        section.setUuid(UUID.randomUUID());
        section.setActive(active);
        route.addSectionPrice(section);
        return section;
    }

    private SectionPriceCreate request(UUID from, UUID to, BigDecimal price) {
        return new SectionPriceCreate(from, to, price);
    }

    private void givenPersistenceAndMapping(SectionPriceDTO expectedDTO) {
        when(sectionPriceRepository.save(any(SectionPrice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(sectionPriceMapper.toDTO(any(SectionPrice.class))).thenReturn(expectedDTO);
    }

    @Test
    void shouldCreateFirstSectionStartingAtFirstPoint() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");

        SectionPriceDTO expectedDTO = new SectionPriceDTO();
        givenPersistenceAndMapping(expectedDTO);

        SectionPriceDTO result = sectionPriceService.createSectionPrice(
                company.getUuid(), route.getUuid(),
                request(a.getUuid(), b.getUuid(), BigDecimal.valueOf(1000)));

        assertSame(expectedDTO, result);

        ArgumentCaptor<SectionPrice> captor = ArgumentCaptor.forClass(SectionPrice.class);
        verify(sectionPriceRepository).save(captor.capture());

        SectionPrice saved = captor.getValue();
        assertSame(route, saved.getRoute());
        assertSame(a, saved.getFromPoint());
        assertSame(b, saved.getToPoint());
        assertEquals(BigDecimal.valueOf(1000), saved.getPrice());
        assertTrue(saved.isActive());
        assertEquals(List.of(saved), route.getSectionPrices());

        verify(routeRepository).save(route);
        verify(sectionPriceMapper).toDTO(saved);
    }

    @Test
    void shouldAppendContinuousSectionAfterLastActiveSection() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        givenSection(a, b, BigDecimal.valueOf(1000), true);

        givenPersistenceAndMapping(new SectionPriceDTO());

        sectionPriceService.createSectionPrice(
                company.getUuid(), route.getUuid(),
                request(b.getUuid(), c.getUuid(), BigDecimal.valueOf(2000)));

        ArgumentCaptor<SectionPrice> captor = ArgumentCaptor.forClass(SectionPrice.class);
        verify(sectionPriceRepository).save(captor.capture());

        SectionPrice saved = captor.getValue();
        assertSame(b, saved.getFromPoint());
        assertSame(c, saved.getToPoint());
        assertEquals(BigDecimal.valueOf(2000), saved.getPrice());
        assertTrue(saved.isActive());
        assertEquals(2, route.getSectionPrices().size());
    }

    @Test
    void shouldReactivateInactiveSectionInsteadOfCreatingNewOne() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        SectionPrice inactive = givenSection(a, b, BigDecimal.valueOf(500), false);

        givenPersistenceAndMapping(new SectionPriceDTO());

        sectionPriceService.createSectionPrice(
                company.getUuid(), route.getUuid(),
                request(a.getUuid(), b.getUuid(), BigDecimal.valueOf(1000)));

        ArgumentCaptor<SectionPrice> captor = ArgumentCaptor.forClass(SectionPrice.class);
        verify(sectionPriceRepository).save(captor.capture());

        // Se reutiliza la misma instancia: no se agrega un tramo duplicado a la ruta
        SectionPrice saved = captor.getValue();
        assertSame(inactive, saved);
        assertTrue(saved.isActive());
        assertEquals(BigDecimal.valueOf(1000), saved.getPrice());
        assertEquals(1, route.getSectionPrices().size());

        verify(routeRepository).save(route);
    }

    @Test
    void shouldThrowConflictAndNotSaveWhenSectionAlreadyActive() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        givenSection(a, b, BigDecimal.valueOf(1000), true);

        assertThrows(
                ConflictException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(a.getUuid(), b.getUuid(), BigDecimal.valueOf(1500)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowConflictAndNotSaveWhenNewSectionOverlapsActiveSection() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        givenSection(a, b, BigDecimal.valueOf(1000), true);

        // A -> C abarca el tramo A -> B ya existente
        assertThrows(
                ConflictException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(a.getUuid(), c.getUuid(), BigDecimal.valueOf(2000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowBadRequestWhenDestinationIsNotAfterOrigin() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");

        assertThrows(
                BadRequestException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(b.getUuid(), a.getUuid(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowBadRequestWhenFirstSectionDoesNotStartAtFirstPoint() {

        givenRouteExists();
        givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");

        assertThrows(
                BadRequestException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(b.getUuid(), c.getUuid(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowBadRequestWhenSectionIsNotContinuous() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");
        RoutePoint b = givenPoint("B");
        RoutePoint c = givenPoint("C");
        RoutePoint d = givenPoint("D");
        givenSection(a, b, BigDecimal.valueOf(1000), true);

        // C -> D no comienza donde terminó A -> B (en B)
        assertThrows(
                BadRequestException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(c.getUuid(), d.getUuid(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowNotFoundWhenPointDoesNotBelongToRoute() {

        givenRouteExists();
        RoutePoint a = givenPoint("A");

        assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(a.getUuid(), UUID.randomUUID(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowConflictWhenRouteHasNoPoints() {

        givenRouteExists();

        assertThrows(
                ConflictException.class,
                () -> sectionPriceService.createSectionPrice(
                        company.getUuid(), route.getUuid(),
                        request(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void shouldThrowNotFoundAndNotSaveWhenRouteDoesNotExist() {

        UUID companyUuid = UUID.randomUUID();
        UUID routeUuid = UUID.randomUUID();

        when(routeRepository.findByUuidAndCompanyUuidAndCompanyActiveTrueAndActiveTrue(routeUuid, companyUuid))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> sectionPriceService.createSectionPrice(
                        companyUuid, routeUuid,
                        request(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(1000)))
                    );

        verify(sectionPriceRepository, never()).save(any(SectionPrice.class));
        verify(routeRepository, never()).save(any(Route.class));
    }
}