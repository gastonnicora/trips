package com.gastonnicora.trips.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.UserRepository;

/**
 * Implementación de {@link UserDetailsService} de Spring Security.
 * <p>
 * Se encarga de cargar la información del usuario desde la base de datos
 * para la autenticación.
 * </p>
 * <p>
 * Utiliza {@link UserRepository} para buscar usuarios activos por email.
 * Retorna un {@link UserDetailsImpl} para que Spring Security gestione
 * la autenticación y autorización.
 * </p>
 * 
 * Flujo principal:
 * <ol>
 * <li>Recibe un username (email) a autenticar.</li>
 * <li>Busca el usuario en la base de datos con
 * {@link UserRepository#findByEmailAndEnabledTrue(String)}.</li>
 * <li>Si no encuentra el usuario, lanza {@link UsernameNotFoundException}.</li>
 * <li>Si lo encuentra, retorna un {@link UserDetailsImpl} que envuelve al
 * usuario.</li>
 * </ol>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario por su username (email) para autenticación.
     * 
     * @param username Email del usuario a autenticar
     * @return {@link UserDetails} con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe o no está
     *                                   habilitado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserDetailsImpl(user);
    }

}