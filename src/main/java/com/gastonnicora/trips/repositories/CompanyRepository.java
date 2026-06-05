package com.gastonnicora.trips.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Company;

/**
 * Repositorio para gestionar la entidad {@link Company}.
 * <p>
 * Proporciona métodos para consultar empresas por correo electrónico, UUID y
 * dueño entre otras opciones
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByUuid(UUID uuid);



}
