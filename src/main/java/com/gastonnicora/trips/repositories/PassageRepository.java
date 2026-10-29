package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.Passage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassageRepository extends JpaRepository<Passage, UUID> {
}
