package com.gastonnicora.trips.services;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.User.UserChangePassword;
import com.gastonnicora.trips.dtos.request.User.UserChangeRole;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ErrorException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio de gestión de usuarios.
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * usuarios,
 * como la creación, actualización, eliminación, y obtención de usuarios.
 * Además,
 * permite cambiar la contraseña y asignar roles a los usuarios.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    /**
     * Constructor que inicializa los servicios necesarios para la gestión de
     * usuarios.
     * 
     * @param userRepository      Repositorio de usuarios utilizado para acceder a
     *                            la base de datos.
     * @param passwordEncoder     Codificador de contraseñas para proteger las
     *                            contraseñas de los usuarios.
     * @param refreshTokenService Servicio para manejar los tokens de refresco de
     *                            los usuarios.
     */
    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Se valida que el correo electrónico no esté en uso antes de crear el nuevo
     * usuario.
     * El sistema cifra la contraseña antes de guardarla.
     * Si el correo ya está en uso, se lanza una excepción {@link ErrorException}.
     * </p>
     * 
     * @param userCreate ({@link UserCreate}) que contiene la información
     *                   para crear el nuevo usuario.
     * @return {@link UserDTO} Datos del usuario recién creado.
     * @throws ErrorException Si el correo electrónico ya está siendo utilizado.
     */
    public UserDTO createUser(UserCreate userCreate) {
        if (userRepository.existsByEmailAndEnabledTrue(userCreate.getEmail())) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("email", "El email ya esta siendo utilizado");
            throw ex;
        }
        userCreate.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        User newUser = new User(
                userCreate.getName(),
                userCreate.getLastname(),
                userCreate.getEmail(),
                userCreate.getPassword(),
                Set.of(Role.USER));

        return userMapper.toDTO(userRepository.save(newUser));
    }

    /**
     * Obtiene los datos del usuario actual.
     * <p>
     * Se busca el usuario mediante su UUID y se devuelve un objeto {@link UserDTO}
     * con los detalles del usuario actual.
     * Si el usuario no es encontrado, se lanza una excepción
     * {@link ErrorException}.
     * </p>
     * 
     * @return {@link UserDTO} Datos del usuario actual.
     * @throws ErrorException Si el usuario actual no es encontrado en la base de
     *                        datos.
     */
    // WARNING ⚠️: Repeticion de codigo(getUsersByUUid)
    public UserDTO getCurrentUser() {
        Optional<User> user = userRepository.findByUuid(getCurrentUserUuid());
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        ErrorException ex = new ErrorException("El usuario no se encontró", 400);
        throw ex;
    }

    /**
     * Obtiene los detalles de un usuario específico basado en su UUID.
     * <p>
     * Si el usuario no existe, se lanza una excepción {@link ErrorException}.
     * </p>
     * 
     * @param uuid UUID del usuario que se quiere obtener.
     * @return {@link UserDTO} Datos del usuario con el UUID especificado.
     * @throws ErrorException Si el usuario no existe.
     */
    // WARNING ⚠️: codigo repetido (getCurrentUser)
    public UserDTO getUserByUuid(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        ErrorException ex = new ErrorException("El usuario buscado no existe", 400);
        throw ex;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * <p>
     * Devuelve una lista ({@link ListResponse}) con los datos de todos los
     * usuarios. Utiliza
     * {@link UserMapper} para convertir
     * las entidades de {@link User} a DTOs {@link UserDTO}.
     * </p>
     * 
     * @return {@link ListResponse} Una lista de {@link UserDTO} con todos los
     *         usuarios.
     */

    public ListResponse<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();
        return new ListResponse<UserDTO>(userMapper.toDTOList(users));
    }

    /**
     * Actualiza los detalles de un usuario específico identificado por su UUID.
     * <p>
     * Si el email del usuario cambia, se verifica que no esté en uso por otro
     * usuario.
     * Si el cambio es exitoso, se desactivan todos los tokens de refresco asociados
     * con el usuario.
     * </p>
     * 
     * @param uuid    UUID del usuario a actualizar.
     * @param userPut ({@link UserPut}) con los nuevos datos del usuario.
     * @return {@link UserDTO} Datos actualizados del usuario.
     * @throws ErrorException Si el usuario no existe o el email ya está en uso.
     */
    // WARNING ⚠️:Repeticion de codigo(updateCurrentUser)
    @Transactional
    public UserDTO updateUserByUuid(UUID uuid, UserPut userPut) {
        User userEntity = userRepository.findByUuid(uuid).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userEntity.setName(userPut.getName());
        userEntity.setLastname(userPut.getLastname());
        if (!userPut.getEmail().equals(userEntity.getEmail())) {
            if (userRepository.existsByEmailAndEnabledTrue(userPut.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");
                throw ex;
            }
            userEntity.setEmail(userPut.getEmail());
            userEntity.addVersion();

            refreshTokenService.deactivateAllByUserUuid(userEntity.getUuid());
        }
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);

    }

    /**
     * Actualiza los detalles del usuario actual.
     * <p>
     * Si el email del usuario cambia, se verifica que no esté en uso por otro
     * usuario.
     * Si el cambio es exitoso, se desactivan todos los tokens de refresco asociados
     * con el usuario.
     * </p>
     * 
     * @param userPut ({@link UserPut}) con los nuevos datos del usuario.
     * @return {@link UserDTO} Datos actualizados del usuario.
     * @throws ErrorException Si el usuario no existe o el email ya está en uso.
     */
    @Transactional
    public UserDTO updateCurrentUser(UserPut userPut) {
        User userEntity = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userEntity.setName(userPut.getName());
        userEntity.setLastname(userPut.getLastname());
        if (!userPut.getEmail().equals(userEntity.getEmail())) {
            if (userRepository.existsByEmailAndEnabledTrue(userPut.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");

                throw ex;
            }
            userEntity.setEmail(userPut.getEmail());
            userEntity.addVersion();

            refreshTokenService.deactivateAllByUserUuid(userEntity.getUuid());
        }
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);

    }

    /**
     * Cambia la contraseña del usuario actual.
     * <p>
     * Se verifica que la contraseña actual proporcionada coincida con la
     * almacenada. Si no es correcta,
     * se lanza una excepción {@link ErrorException}. Luego, se actualiza la
     * contraseña y se desactivan
     * todos los tokens de refresco.
     * </p>
     * 
     * @param userChangePassword ({@link UserChangePassword}) con las nuevas
     *                           credenciales.
     * @return {@link UserDTO} Datos del usuario con la contraseña actualizada.
     * @throws ErrorException Si la contraseña actual es incorrecta.
     */

    @Transactional
    public UserDTO updatePassword(UserChangePassword userChangePassword) {
        User userEntity = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        if (!passwordEncoder.matches(userChangePassword.getPasswordOld(), userEntity.getPassword())) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("passwordOld", "La contraseña actual es incorrecta");

            throw ex;
        }
        userEntity.setPassword(passwordEncoder.encode(userChangePassword.getPassword()));
        userEntity.addVersion();
        userRepository.save(userEntity);
        refreshTokenService.deactivateAllByUserUuid(userEntity.getUuid());

        return userMapper.toDTO(userEntity);
    }

    /**
     * Asigna roles a un usuario.
     * <p>
     * Este método asigna un conjunto de roles al usuario identificado por su UUID.
     * No se puede cambiar el rol de un usuario con rol {@link Role#SUPER_ADMIN}, ni
     * asignar el rol de {@link Role#SUPER_ADMIN}.
     * </p>
     * 
     * @param uuid UUID del usuario a actualizar.
     * @param role ({@link UserChangeRole}) que contiene los nuevos roles para el
     *             usuario.
     * @return {@link UserDTO} Datos del usuario con los roles actualizados.
     * @throws ErrorException Si el usuario no existe o si se intenta cambiar o
     *                        asignar el rol de {@link Role#SUPER_ADMIN}.
     */
    public UserDTO setRole(UUID uuid, UserChangeRole role) {
        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        if (user.getRole().contains(Role.SUPER_ADMIN)) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("role", "No se puede modificar los roles del SUPER_ADMIN");
            throw ex;
        }
        if (!role.getRoles().contains(Role.USER)) {
            role.getRoles().add(Role.USER);
        }
        role.getRoles().remove(Role.SUPER_ADMIN);
        user.setRole(role.getRoles());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    /**
     * Elimina el usuario actual.
     * <p>
     * Este método desactiva al usuario actual y marca su estado como deshabilitado
     * en la base de datos. Además, cierra todas las sesiones activas del usuario
     * mediante la desactivación de sus tokens de refresco.
     * </p>
     * 
     * @throws ErrorException Si el usuario no existe en la base de datos.
     */

    @Transactional
    public void deleteCurrentUser() {
        User user = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        user.setEnabled(false);
        user.addVersion();
        userRepository.save(user);
        refreshTokenService.deactivateAllByUserUuid(user.getUuid());
    }

    /**
     * Crea un SUPER_ADMIN si no existe en la base de datos.
     * <p>
     * Este método verifica si ya existe un usuario con el rol de SUPER_ADMIN.
     * Si no existe, crea un nuevo usuario con el rol {@link Role#SUPER_ADMIN}.
     * El email y la contraseña se proporcionan como parámetros.
     * </p>
     *
     * @param email    String con el email del SUPER_ADMIN a crear.
     * @param password String con la contraseña del SUPER_ADMIN a crear.
     * @throws ErrorException Si ya existe un SUPER_ADMIN o si los parámetros
     *                        de entrada son inválidos.
     */

    public void createSuperAdminIfNotExists(String email, String password) {
        boolean exists = userRepository.existsByRoleContains(Role.SUPER_ADMIN);

        if (!exists && email != null && password != null && !userRepository.existsByEmailAndEnabledTrue(email)) {
            User superAdmin = new User(
                    "Super",
                    "Admin",
                    email,
                    passwordEncoder.encode(password),
                    Set.of(Role.SUPER_ADMIN));

            userRepository.save(superAdmin);
        }
    }
}
