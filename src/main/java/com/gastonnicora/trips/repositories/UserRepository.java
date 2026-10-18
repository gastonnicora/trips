package com.gastonnicora.trips.repositories;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio encargado de gestionar la persistencia de entidades {@link User}.
 *
 * <p>
 * Proporciona operaciones para consultar usuarios por correo electrónico,
 * identificador único, estado de habilitación y rol.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Verifica si existe un usuario habilitado con el correo electrónico indicado.
     *
     * @param email Correo electrónico del usuario.
     *
     * @return {@code true} si existe un usuario habilitado con el correo indicado;
     * {@code false} en caso contrario.
     */
    boolean existsByEmailAndEnabledTrue(String email);

    /**
     * Verifica si existe al menos un usuario que contenga el rol indicado.
     *
     * @param role Rol que se desea verificar.
     *
     * @return {@code true} si existe al menos un usuario con el rol indicado;
     * {@code false} en caso contrario.
     */
    boolean existsByRoleContains(Role role);

    /**
     * Busca todos los usuarios que coinciden con el correo electrónico indicado,
     * independientemente de su estado de habilitación.
     *
     * @param email Correo electrónico del usuario.
     *
     * @return Lista de usuarios que coinciden con el correo indicado.
     */
    List<User> findByEmail(String email);

    /**
     * Busca usuarios por su correo electrónico y estado de habilitación.
     *
     * @param email   Correo electrónico del usuario.
     * @param enabled Estado de habilitación del usuario.
     *
     * @return Lista de usuarios que coinciden con el correo electrónico y estado
     * indicados.
     */
    List<User> findByEmailAndEnabled(String email, boolean enabled);

    /**
     * Busca un usuario por su correo electrónico únicamente si se encuentra
     * habilitado.
     *
     * @param email Correo electrónico del usuario.
     *
     * @return {@link Optional} que contiene el usuario encontrado, o vacío si no
     * existe
     * un usuario habilitado con el correo indicado.
     */
    Optional<User> findByEmailAndEnabledTrue(String email);

    /**
     * Busca un usuario por su identificador único.
     *
     * @param uuid Identificador único del usuario.
     *
     * @return {@link Optional} que contiene el usuario encontrado, o vacío si no
     * existe.
     */
    Optional<User> findByUuid(UUID uuid);

}