package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoutePointRepository extends JpaRepository<RoutePoint, UUID> {

    Optional<RoutePoint> findByUuid(UUID uuid);
}
