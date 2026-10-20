package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface RouteRepository extends JpaRepository<Route, UUID> {

    Optional<Route> findByUuidAndActiveTrue(UUID uuid);
}
