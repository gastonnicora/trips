package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndEnabled(String email, boolean enabled);

    Optional<User> findByEmailAndEnabledTrue(String email);

    Boolean existsByEmailAndEnabledTrue(String email);


    List<User> findByEmail(String email);

    Optional<User> findByUuid(UUID uuid);

    boolean existsByRoleContains(Role role);
}
