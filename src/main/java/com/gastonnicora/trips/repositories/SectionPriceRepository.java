package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.SectionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link SectionPrice}.
 *
 * <p>
 * Proporciona operaciones para consultar los precios por tramo de una ruta por
 * identificador único.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-20
 */
public interface SectionPriceRepository extends JpaRepository<SectionPrice, UUID> {

    /**
     * Busca un precio por tramo por su identificador único.
     *
     * @param uuid Identificador único del precio por tramo.
     *
     * @return {@link Optional} que contiene el precio por tramo encontrado, o vacío
     * si no existe.
     */
    Optional<SectionPrice> findByUuid(UUID uuid);
}
