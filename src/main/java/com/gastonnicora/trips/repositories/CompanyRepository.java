package com.gastonnicora.trips.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.Company;

/**
 * Repositorio encargado de gestionar la persistencia de entidades
 * {@link Company}.
 *
 * <p>
 * Extiende {@link JpaRepository} para proporcionar las operaciones de acceso
 * y gestión de datos correspondientes a la entidad empresa.
 * </p>
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    /**
     * Busca una empresa por su identificador único que este activo.
     *
     * @param uuid Identificador único de la empresa.
     * @return {@link Optional} que contiene la empresa encontrada, o vacío si no existe.
     */
    Optional<Company> findByUuidAndActiveTrue(UUID uuid);

}