package com.gastonnicora.trips.security;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gastonnicora.trips.entities.User;

/**
 * Implementación de {@link UserDetails} de Spring Security.
 * <p>
 * Permite que Spring Security gestione la autenticación y autorización
 * a partir de la entidad {@link User}.
 * </p>
 * 
 * Funcionalidades:
 * <ul>
 * <li>Devuelve las credenciales del usuario (email y password).</li>
 * <li>Proporciona los roles del usuario como {@link GrantedAuthority}.</li>
 * <li>Controla el estado de la cuenta (habilitado, no bloqueado, no
 * expirado).</li>
 * </ul>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
public class UserDetailsImpl implements UserDetails {
    private final User user;

    /**
     * Constructor que recibe la entidad {@link User}.
     * 
     * @param user Usuario de la aplicación
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Obtiene el UUID del usuario.
     * 
     * @return UUID del usuario
     */
    public UUID getUuid() {
        return user.getUuid();
    }

    /**
     * Obtiene los roles del usuario como {@link GrantedAuthority}.
     * <p>
     * Cada rol se convierte en un authority con el prefijo "ROLE_".
     * </p>
     * 
     * @return Colección de authorities
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
     * @return Contraseña cifrada
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Obtiene el nombre de usuario para autenticación.
     * <p>
     * En este caso, es el email del usuario.
     * </p>
     * 
     * @return Email del usuario
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indica si la cuenta del usuario no ha expirado.
     * 
     * @return true siempre, ya que la expiración no se gestiona
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario no está bloqueada.
     * 
     * @return true siempre, ya que el bloqueo no se gestiona
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario no han expirado.
     * 
     * @return true siempre, ya que la expiración de credenciales no se gestiona
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado.
     * 
     * @return true si el usuario está habilitado, false en caso contrario
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}