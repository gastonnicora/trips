package com.gastonnicora.trips.services;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.ValidationException;
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

    private final UserMapper userMapper;

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
     * 
     * @param userMapper          Mapper para convertir entidades {@link User} a
     *                            DTOs {@link UserDTO}.
     */
    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Se valida que el correo electrónico no esté en uso antes de crear el nuevo
     * usuario.
     * El sistema cifra la contraseña antes de guardarla.
     * Si el correo ya está en uso, se lanza una excepción
     * {@link ConflictException}.
     * </p>
     * 
     * @param userCreate ({@link UserCreate}) que contiene la información
     *                   para crear el nuevo usuario.
     * @return {@link UserDTO} Datos del usuario recién creado.
     * @throws ConflictException Si el correo electrónico ya está siendo utilizado.
     */
    public UserDTO createUser(UserCreate userCreate) {
        if (userRepository.existsByEmailAndEnabledTrue(userCreate.getEmail())) {
            throw new ConflictException("El email ya esta en uso");
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
     * {@link NotFoundException}.
     * </p>
     * 
     * @return {@link UserDTO} Datos del usuario actual.
     * @throws NotFoundException Si el usuario actual no es encontrado en la base de
     *                           datos.
     */
    public UserDTO getCurrentUser() {
        return userMapper.toDTO(getUser(getCurrentUserUuid()));
    }

    /**
     * Obtiene los detalles de un usuario específico basado en su UUID.
     * <p>
     * Si el usuario no existe, se lanza una excepción {@link NotFoundException}.
     * </p>
     * 
     * @param uuid UUID del usuario que se quiere obtener.
     * @return {@link UserDTO} Datos del usuario con el UUID especificado.
     * @throws NotFoundException Si el usuario no existe.
     */
    public UserDTO getUserByUuid(UUID uuid) {
        return userMapper.toDTO(getUser(uuid));
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
     * @throws NotFoundException Si el usuario no existe.
     * @throws ConflictException Si el email ya está en uso.
     */
    public UserDTO updateUserByUuid(UUID uuid, UserPut userPut) {
        return updateUser(uuid, userPut);
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
     * @throws NotFoundException Si el usuario no existe.
     * @throws ConflictException Si el email ya está en uso.
     */
    public UserDTO updateCurrentUser(UserPut userPut) {
        return updateUser(getCurrentUserUuid(), userPut);
    }

    /**
     * Cambia la contraseña del usuario actual.
     * <p>
     * Se verifica que la contraseña actual proporcionada coincida con la
     * almacenada. Si no es correcta,
     * se lanza una excepción {@link ValidationException}. Luego, se actualiza la
     * contraseña y se desactivan
     * todos los tokens de refresco.
     * </p>
     * 
     * @param userChangePassword ({@link UserChangePassword}) con las nuevas
     *                           credenciales.
     * @return {@link UserDTO} Datos del usuario con la contraseña actualizada.
     * @throws ValidationException Si la contraseña actual es incorrecta.
     * @throws NotFoundException   Si el usuario no existe.
     */

    @Transactional
    public UserDTO updatePassword(UserChangePassword userChangePassword) {
        User userEntity = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(
                () -> new NotFoundException("El usuario solicitado no existe"));
        if (!passwordEncoder.matches(userChangePassword.getPasswordOld(), userEntity.getPassword())) {
            ValidationException ex = new ValidationException("Error en la validación");
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
     * @throws NotFoundException   Si el usuario no existe.
     * @throws ValidationException si se intenta cambiar el rol de
     *                             {@link Role#SUPER_ADMIN}.
     */
    public UserDTO setRole(UUID uuid, UserChangeRole role) {
        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException("El usuario solicitado no existe"));
        if (user.getRole().contains(Role.SUPER_ADMIN)) {
            ValidationException ex = new ValidationException("Error en la validación");
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
     * @throws NotFoundException Si el usuario no existe en la base de datos.
     */

    @Transactional
    public void deleteCurrentUser() {
        User user = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(
                () -> new NotFoundException("El usuario solicitado no existe"));
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

    /**
     * Actualiza los detalles de un usuario dado por UUID.
     * <p>
     * Este método realiza las siguientes operaciones:
     * </p>
     * <ul>
     * <li>Busca al usuario por UUID; lanza {@link NotFoundException} si no
     * existe.</li>
     * <li>Actualiza el nombre y apellido del usuario.</li>
     * <li>Si el email cambia:
     * <ul>
     * <li>Verifica que no esté en uso por otro usuario activo; lanza
     * {@link ConflictException} si ya existe.</li>
     * <li>Actualiza el email del usuario.</li>
     * <li>Incrementa la versión del usuario (método {@code addVersion()}).</li>
     * <li>Desactiva todos los tokens de refresco asociados al usuario mediante
     * {@link #refreshTokenService}.</li>
     * </ul>
     * </li>
     * <li>Guarda los cambios en la base de datos y devuelve un {@link UserDTO}
     * actualizado.</li>
     * </ul>
     * 
     * 
     * @param uuid    UUID del usuario a actualizar
     * @param userPut {@link UserPut} con los nuevos datos del usuario
     * @return {@link UserDTO} con los datos actualizados
     * @throws NotFoundException si el usuario con el UUID dado no existe
     * @throws ConflictException si se intenta cambiar el email a uno que ya está en
     *                           uso
     */
    @Transactional
    private UserDTO updateUser(UUID uuid, UserPut userPut) {
        User userEntity = userRepository.findByUuid(uuid).orElseThrow(
                () -> new NotFoundException("El usuario solicitado no existe"));
        userEntity.setName(userPut.getName());
        userEntity.setLastname(userPut.getLastname());
        if (!userPut.getEmail().equals(userEntity.getEmail())) {
            if (userRepository.existsByEmailAndEnabledTrue(userPut.getEmail())) {
                throw new ConflictException("El email ya esta en uso");
            }
            userEntity.setEmail(userPut.getEmail());
            userEntity.addVersion();

            refreshTokenService.deactivateAllByUserUuid(userEntity.getUuid());
        }
        userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
    }

    /**
     * Busca un usuario por su UUID y lo convierte a {@link UserDTO}.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca al usuario en la base de datos mediante
     * {@link #userRepository}.</li>
     * <li>Si no se encuentra, lanza una {@link NotFoundException} con mensaje
     * descriptivo.</li>
     * </ul>
     * 
     * 
     * @param uuid UUID del usuario que se quiere obtener
     * @return {@link User} Datos del usuario correspondiente
     * @throws NotFoundException si no existe ningún usuario con el UUID
     *                           proporcionado
     */
    public User getUser(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return user.get();
        }
        throw new NotFoundException("El usuario solicitado no existe");
    }
}
