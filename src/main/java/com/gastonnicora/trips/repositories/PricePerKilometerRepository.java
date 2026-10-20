package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.PricePerKilometer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PricePerKilometerRepository extends JpaRepository<PricePerKilometer, UUID> {

    Optional<PricePerKilometer> findByUuid(UUID uuid);
}
