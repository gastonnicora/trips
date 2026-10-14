package com.gastonnicora.trips.security;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gastonnicora.trips.entities.User;

/**
 * Implementación de {@link UserDetails} de Spring Security basada en la
 * entidad {@link User}.
 *
 * <p>
 * Proporciona las credenciales, authorities y estado de la cuenta necesarios
 * para que Spring Security gestione la autenticación y autorización del
 * usuario.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
public class UserDetailsImpl implements UserDetails {

    private final User user;

    /**
     * Crea una instancia de los detalles de usuario a partir de una entidad
     * {@link User}.
     *
     * @param user Usuario de la aplicación ({@link User}).
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Obtiene el identificador único del usuario.
     *
     * @return UUID del usuario.
     */
    public UUID getUuid() {
        return user.getUuid();
    }

    /**
     * Obtiene los roles del usuario convertidos en authorities de Spring
     * Security.
     *
     * <p>
     * Cada rol se transforma en un {@link SimpleGrantedAuthority} utilizando
     * el prefijo {@code ROLE_}.
     * </p>
     *
     * @return Colección de authorities asociadas al usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Obtiene el nombre de usuario utilizado para la autenticación.
     *
     * <p>
     * En esta implementación, el nombre de usuario corresponde al correo
     * electrónico del usuario.
     * </p>
     *
     * @return Correo electrónico del usuario.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indica si la cuenta del usuario no ha expirado.
     *
     * <p>
     * Esta implementación devuelve siempre {@code true}, ya que la expiración
     * de la cuenta no se gestiona.
     * </p>
     *
     * @return {@code true} siempre.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario no está bloqueada.
     *
     * <p>
     * Esta implementación devuelve siempre {@code true}, ya que el bloqueo de
     * la cuenta no se gestiona.
     * </p>
     *
     * @return {@code true} siempre.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario no han expirado.
     *
     * <p>
     * Esta implementación devuelve siempre {@code true}, ya que la expiración
     * de las credenciales no se gestiona.
     * </p>
     *
     * @return {@code true} siempre.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario se encuentra habilitado.
     *
     * @return {@code true} si el usuario está habilitado; {@code false} en caso
     *         contrario.
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}