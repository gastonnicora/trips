package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.PricePerKilometer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link PricePerKilometer}.
 *
 * <p>
 * Proporciona operaciones para consultar la configuración de precio por
 * kilómetro de una ruta por identificador único.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-10-20
 */
public interface PricePerKilometerRepository extends JpaRepository<PricePerKilometer, UUID> {

    /**
     * Busca la configuración de precio por kilómetro por su identificador único.
     *
     * @param uuid Identificador único de la configuración de precio por kilómetro.
     *
     * @return {@link Optional} que contiene la configuración encontrada, o vacío si
     * no existe.
     */
    Optional<PricePerKilometer> findByUuid(UUID uuid);
}
