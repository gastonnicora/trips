package com.gastonnicora.trips.security;

import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de {@link UserDetailsService} de Spring Security.
 *
 * <p>
 * Se encarga de cargar la información de un usuario desde la base de datos
 * para su autenticación.
 * </p>
 *
 * <p>
 * Utiliza {@link UserRepository} para buscar usuarios habilitados por correo
 * electrónico y retorna un {@link UserDetailsImpl} que permite a Spring
 * Security gestionar la autenticación y autorización.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-03-18
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Construye una instancia de {@code UserDetailsServiceImpl}.
     *
     * <p>
     * Inicializa el servicio con el repositorio utilizado para buscar usuarios
     * habilitados durante el proceso de autenticación.
     * </p>
     *
     * @param userRepository Repositorio ({@link UserRepository}) utilizado para consultar los usuarios
     *                       por correo electrónico y estado de habilitación.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario para el proceso de autenticación.
     *
     * <p>
     * En esta implementación, el nombre de usuario corresponde al correo
     * electrónico del usuario. La búsqueda se realiza únicamente entre los
     * usuarios habilitados.
     * </p>
     *
     * @param username Correo electrónico del usuario que se desea autenticar.
     *
     * @return {@link UserDetails} con la información del usuario encontrado.
     *
     * @throws UsernameNotFoundException Si no existe un usuario habilitado con el
     *                                   correo electrónico indicado.
     * @see UserRepository#findByEmailAndEnabledTrue(String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserDetailsImpl(user);
    }

}