package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.SectionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SectionPriceRepository extends JpaRepository<SectionPrice, UUID> {

    Optional<SectionPrice> findByUuid(UUID uuid);
}
