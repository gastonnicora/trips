package com.gastonnicora.trips.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;

/**
 * Repositorio para gestionar la entidad {@link User}.
 * <p>
 * Proporciona métodos para consultar usuarios por correo electrónico, UUID y roles.
 * Utiliza Spring Data JPA para el acceso a la base de datos.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su correo electrónico y su estado de habilitación.
     * 
     * @param email Correo electrónico
     * @param enabled Estado de habilitación
     * @return {@link Optional} con el usuario si existe
     */
    Optional<User> findByEmailAndEnabled(String email, boolean enabled);

    /**
     * Busca un usuario por su correo electrónico solo si está habilitado.
     * 
     * @param email Correo electrónico
     * @return {@link Optional} con el usuario si existe y está habilitado
     */
    Optional<User> findByEmailAndEnabledTrue(String email);

    /**
     * Verifica si existe un usuario habilitado con el correo electrónico indicado.
     * 
     * @param email Correo electrónico
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmailAndEnabledTrue(String email);

    /**
     * Busca todos los usuarios con el correo electrónico indicado, independientemente de su estado.
     * 
     * @param email Correo electrónico
     * @return Lista de usuarios encontrados
     */
    List<User> findByEmail(String email);

    /**
     * Busca un usuario por su UUID.
     * 
     * @param uuid UUID del usuario
     * @return {@link Optional} con el usuario si existe
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Verifica si existe al menos un usuario con un rol específico.
     * 
     * @param role Rol a verificar
     * @return true si hay usuarios con ese rol, false en caso contrario
     */
    boolean existsByRoleContains(Role role);

}