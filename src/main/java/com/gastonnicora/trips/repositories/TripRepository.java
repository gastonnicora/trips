package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio para gestionar las entidades {@link Trip}.
 *
 * <p>
 * Extiende {@link JpaRepository}, por lo que hereda las operaciones básicas de
 * persistencia. Los identificadores de los viajes son de tipo {@link UUID}.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
public interface TripRepository extends JpaRepository<Trip, UUID> {
}
