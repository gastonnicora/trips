package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.dtos.request.user.UserChangePassword;
import com.gastonnicora.trips.dtos.request.user.UserChangeRole;
import com.gastonnicora.trips.dtos.request.user.UserCreate;
import com.gastonnicora.trips.dtos.request.user.UserPut;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.exceptions.ValidationException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.utils.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los
 * usuarios.
 *
 * <p>
 * Permite crear, consultar, actualizar y desactivar usuarios, además de
 * gestionar sus contraseñas, roles y tokens de refresco asociados.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-03-18
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    /**
     * Crea una instancia del servicio de usuarios e inicializa las dependencias
     * necesarias para la gestión de usuarios, contraseñas, tokens de refresco y
     * conversión de entidades a DTOs.
     *
     * @param userRepository      Repositorio de usuarios utilizado para acceder a
     *                            la
     *                            base de datos ({@link UserRepository}).
     * @param passwordEncoder     Codificador de contraseñas utilizado para proteger
     *                            las contraseñas de los usuarios.
     * @param refreshTokenService Servicio encargado de gestionar los tokens de
     *                            refresco de los usuarios ({@link RefreshTokenService}).
     * @param userMapper          Mapper utilizado para convertir entidades
     *                            {@link User} en
     *                            {@link UserDTO} ({@link UserMapper}).
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
    }

    /**
     * Crea un usuario con el rol {@link Role#SUPER_ADMIN} si todavía no existe
     * uno en la base de datos.
     *
     * <p>
     * Verifica la existencia de un usuario con el rol
     * {@link Role#SUPER_ADMIN} y, si no existe, crea uno utilizando el correo y
     * la contraseña proporcionados. La contraseña se codifica antes de
     * persistirla.
     * </p>
     *
     * @param email    Correo electrónico del usuario SUPER_ADMIN que se desea
     *                 crear.
     * @param password Contraseña del usuario SUPER_ADMIN que se desea crear.
     *
     * @see UserRepository#existsByRoleContains(Role)
     * @see UserRepository#existsByEmailAndEnabledTrue(String)
     */
    public void createSuperAdminIfNotExists(String email, String password) {
        System.out.println("Verificando existencia de SUPER_ADMIN...");
        boolean exists = userRepository.existsByRoleContains(Role.SUPER_ADMIN);
        System.out.println("SUPER_ADMIN existe: " + exists);

        if (!exists && email != null && password != null && !userRepository.existsByEmailAndEnabledTrue(email)) {
            User superAdmin = new User("Super", "Admin", email, passwordEncoder.encode(password), Set.of(Role.SUPER_ADMIN));

            userRepository.save(superAdmin);
        }
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>
     * Antes de crear el usuario, verifica que el correo electrónico no esté en
     * uso. La contraseña se codifica antes de persistir el nuevo usuario y se le
     * asigna el rol {@link Role#USER}.
     * </p>
     *
     * @param userCreate Datos necesarios para crear el nuevo usuario ({@link UserCreate}).
     *
     * @return {@link UserDTO} correspondiente al usuario creado.
     *
     * @throws ConflictException Si el correo electrónico ya está siendo
     *                           utilizado por un usuario activo.
     * @see UserRepository#existsByEmailAndEnabledTrue(String)
     * @see UserMapper#toDTO(User)
     */
    public UserDTO createUser(UserCreate userCreate) {
        if (userRepository.existsByEmailAndEnabledTrue(userCreate.getEmail())) {
            throw new ConflictException("El email ya esta en uso");
        }

        userCreate.setPassword(passwordEncoder.encode(userCreate.getPassword()));

        User newUser = new User(userCreate.getName(), userCreate.getLastname(), userCreate.getEmail(), userCreate.getPassword(), Set.of(Role.USER));

        return userMapper.toDTO(userRepository.save(newUser));
    }

    /**
     * Desactiva el usuario actualmente autenticado.
     *
     * <p>
     * Marca al usuario como deshabilitado, incrementa su versión y desactiva
     * todos los tokens de refresco asociados.
     * </p>
     *
     * @see UserService#getUser(UUID)
     * @see RefreshTokenService#deactivateAllByUserUuid(UUID)
     */
    @Transactional
    public void deleteCurrentUser() {
        User user = this.getUser(getCurrentUserUuid());
        user.setEnabled(false);
        user.addVersion();
        userRepository.save(user);
        refreshTokenService.deactivateAllByUserUuid(user.getUuid());
    }

    /**
     * Obtiene los datos del usuario actualmente autenticado.
     *
     * <p>
     * Obtiene el UUID del usuario actual y busca la entidad correspondiente para
     * convertirla posteriormente en un {@link UserDTO}.
     * </p>
     *
     * @return {@link UserDTO} correspondiente al usuario actual.
     *
     * @see UserMapper#toDTO(User)
     * @see UserService#getUser(UUID)
     * @see SecurityUtils#getCurrentUserUuid()
     */
    public UserDTO getCurrentUser() {
        return userMapper.toDTO(getUser(getCurrentUserUuid()));
    }

    /**
     * Busca un usuario mediante su UUID.
     *
     * <p>
     * Si el usuario existe, devuelve la entidad correspondiente. En caso
     * contrario, lanza una {@link NotFoundException}.
     * </p>
     *
     * @param uuid UUID del usuario que se desea obtener.
     *
     * @return {@link User} correspondiente al UUID proporcionado.
     *
     * @throws NotFoundException Si no existe ningún usuario con el UUID
     *                           proporcionado.
     * @see UserRepository#findByUuid(UUID)
     */
    public User getUser(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);

        if (user.isPresent()) {
            return user.get();
        }

        throw new NotFoundException("El usuario solicitado no existe");
    }

    /**
     * Obtiene los datos de un usuario específico mediante su UUID.
     *
     * @param uuid UUID del usuario que se desea obtener.
     *
     * @return {@link UserDTO} correspondiente al usuario solicitado.
     *
     * @see UserMapper#toDTO(User)
     * @see UserService#getUser(UUID)
     */
    public UserDTO getUserByUuid(UUID uuid) {
        return userMapper.toDTO(getUser(uuid));
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * <p>
     * Las entidades obtenidas del repositorio se convierten en
     * {@link UserDTO} mediante {@link UserMapper} y se encapsulan en un
     * {@link ListResponse}.
     * </p>
     *
     * @return {@link ListResponse} que contiene los datos de todos los usuarios.
     *
     * @see ListResponse
     * @see UserMapper#toDTOList(List)
     */
    public ListResponse<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();
        return new ListResponse<>(userMapper.toDTOList(users));
    }

    /**
     * Asigna nuevos roles a un usuario identificado mediante su UUID.
     *
     * <p>
     * No permite modificar los roles de un usuario que posee el rol
     * {@link Role#SUPER_ADMIN} ni asignar dicho rol mediante este método.
     * Además, garantiza que el usuario mantenga el rol {@link Role#USER}.
     * </p>
     *
     * @param uuid UUID del usuario cuyos roles se desean actualizar.
     * @param role Datos que contienen los nuevos roles del usuario ({@link UserChangeRole}).
     *
     * @return {@link UserDTO} correspondiente al usuario con los roles
     * actualizados.
     *
     * @throws ValidationException Si se intenta modificar los roles de un usuario
     *                             con rol {@link Role#SUPER_ADMIN}.
     * @see UserService#getUser(UUID)
     * @see UserMapper#toDTO(User)
     */
    public UserDTO setRole(UUID uuid, UserChangeRole role) {
        User user = this.getUser(uuid);

        if (user.getRole().contains(Role.SUPER_ADMIN)) {
            ValidationException ex = new ValidationException("Error en la validación");
            ex.addError("role", "No se puede modificar los roles del SUPER_ADMIN");
            throw ex;
        }

        role.getRoles().add(Role.USER);

        role.getRoles().remove(Role.SUPER_ADMIN);
        user.setRole(role.getRoles());
        userRepository.save(user);

        return userMapper.toDTO(user);
    }

    /**
     * Actualiza los datos del usuario actualmente autenticado.
     *
     * <p>
     * Si se modifica el correo electrónico, verifica que el nuevo correo no esté
     * en uso por otro usuario activo. Cuando el correo cambia, se incrementa la
     * versión del usuario y se desactivan todos sus tokens de refresco.
     * </p>
     *
     * @param userPut Datos con los nuevos valores del usuario ({@link UserPut}).
     *
     * @return {@link UserDTO} correspondiente al usuario actualizado.
     *
     * @see UserService#updateUser(UUID, UserPut)
     * @see SecurityUtils#getCurrentUserUuid()
     */
    @Transactional
    public UserDTO updateCurrentUser(UserPut userPut) {
        return updateUser(getCurrentUserUuid(), userPut);
    }

    /**
     * Actualiza la contraseña del usuario actualmente autenticado.
     *
     * <p>
     * Verifica que la contraseña actual proporcionada coincida con la almacenada.
     * Si la validación es correcta, actualiza la contraseña, incrementa la
     * versión del usuario y desactiva todos sus tokens de refresco.
     * </p>
     *
     * @param userChangePassword Datos que contienen las credenciales necesarias
     *                           para realizar el cambio de contraseña ({@link UserChangePassword}).
     *
     * @return {@link UserDTO} correspondiente al usuario con la contraseña
     * actualizada.
     *
     * @throws ValidationException Si la contraseña actual proporcionada es
     *                             incorrecta.
     * @throws NotFoundException   Si el usuario no existe.
     * @see UserRepository#findByUuid(UUID)
     * @see RefreshTokenService#deactivateAllByUserUuid(UUID)
     * @see UserMapper#toDTO(User)
     */
    @Transactional
    public UserDTO updatePassword(UserChangePassword userChangePassword) {
        User userEntity = userRepository.findByUuid(getCurrentUserUuid()).orElseThrow(() -> new NotFoundException("El usuario solicitado no existe"));

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
     * Actualiza los datos de un usuario identificado mediante su UUID.
     *
     * <p>
     * Actualiza el nombre y apellido del usuario. Si se modifica el correo
     * electrónico, verifica que no esté en uso por otro usuario activo, actualiza
     * el valor, incrementa la versión del usuario y desactiva todos sus tokens
     * de refresco.
     * </p>
     *
     * <p>
     * Finalmente, persiste los cambios y convierte la entidad actualizada en un
     * {@link UserDTO}.
     * </p>
     *
     * @param uuid    UUID del usuario que se desea actualizar.
     * @param userPut Datos con los nuevos valores del usuario ({@link UserPut}).
     *
     * @return {@link UserDTO} correspondiente al usuario actualizado.
     *
     * @throws ConflictException Si el nuevo correo electrónico ya está en uso.
     * @see UserService#getUser(UUID)
     * @see UserRepository#existsByEmailAndEnabledTrue(String)
     * @see RefreshTokenService#deactivateAllByUserUuid(UUID)
     * @see UserMapper#toDTO(User)
     */
    private UserDTO updateUser(UUID uuid, UserPut userPut) {
        User userEntity = this.getUser(uuid);
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
     * Actualiza los datos de un usuario específico mediante su UUID.
     *
     * <p>
     * Si se modifica el correo electrónico, verifica que el nuevo correo no esté
     * en uso por otro usuario activo. Cuando el correo cambia, se incrementa la
     * versión del usuario y se desactivan todos sus tokens de refresco.
     * </p>
     *
     * @param uuid    UUID del usuario que se desea actualizar.
     * @param userPut Datos con los nuevos valores del usuario ({@link UserPut}).
     *
     * @return {@link UserDTO} correspondiente al usuario actualizado.
     *
     * @see UserService#updateUser(UUID, UserPut)
     */
    @Transactional
    public UserDTO updateUserByUuid(UUID uuid, UserPut userPut) {
        return updateUser(uuid, userPut);
    }

}