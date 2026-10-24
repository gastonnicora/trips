package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
}
