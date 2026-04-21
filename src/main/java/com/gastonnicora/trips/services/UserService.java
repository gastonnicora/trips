package com.gastonnicora.trips.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.dtos.request.User.UserCreate;
import com.gastonnicora.trips.dtos.request.User.UserPassword;
import com.gastonnicora.trips.dtos.request.User.UserPut;
import com.gastonnicora.trips.dtos.request.User.UserRole;
import com.gastonnicora.trips.entitys.User;
import com.gastonnicora.trips.enums.Role;
import com.gastonnicora.trips.exeptions.ErrorException;
import com.gastonnicora.trips.mappers.UserMapper;
import com.gastonnicora.trips.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public UserDTOs createUser(UserCreate user) {
        if (emailUsed(user.getEmail())) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("email", "El email ya esta siendo utilizado");
            throw ex;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = new User(
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                Set.of(Role.USER));

        return userMapper.toDTO(userRepository.save(newUser));
    }

    public UserDTOs getCurrentUser() {
        String email = getCurrentUserEmail();
        Optional<User> user = userRepository.findByEmailAndEnabledTrue(email);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        ErrorException ex = new ErrorException("El usuario no se encontró", 400);
        throw ex;
    }

    public UserDTOs getUserByUuid(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        ErrorException ex = new ErrorException("El usuario buscado no existe", 400);
        throw ex;
    }

    @Transactional
    public UserDTOs putUserByUuid(UUID uuid, UserPut user) {
        User userNow = userRepository.findByUuid(uuid).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        if (!user.getEmail().equals(userNow.getEmail())) {
            if (emailUsed(user.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");
                throw ex;
            }
            userNow.setEmail(user.getEmail());
            userNow.addVersion();

            refreshTokenService.deactivateAllByUserUuid(userNow.getUuid());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);

    }

    @Transactional
    public UserDTOs putCurrentUser(UserPut user) {
        User userNow = userRepository.findByEmailAndEnabledTrue(getCurrentUserEmail()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        if (!user.getEmail().equals(userNow.getEmail())) {
            if (emailUsed(user.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");

                throw ex;
            }
            userNow.setEmail(user.getEmail());
            userNow.addVersion();

            refreshTokenService.deactivateAllByUserUuid(userNow.getUuid());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);

    }

    @Transactional
    public UserDTOs updatePassword(UserPassword user) {
        User userNow = userRepository.findByEmailAndEnabledTrue(getCurrentUserEmail()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        if (!passwordEncoder.matches(user.getPasswordOld(), userNow.getPassword())) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("passwordOld", "La contraseña actual es incorrecta");

            throw ex;
        }
        userNow.setPassword(passwordEncoder.encode(user.getPassword()));
        userNow.addVersion();
        userRepository.save(userNow);
        refreshTokenService.deactivateAllByUserUuid(userNow.getUuid());

        return userMapper.toDTO(userNow);
    }

    public UserDTOs setRole(UUID uuid, UserRole role) {
        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        if (!user.getRole().contains(Role.SUPER_ADMIN)) {
            role.getRoles().remove(Role.SUPER_ADMIN);
        } else {

            throw new ErrorException("No se puede modificar los roles del SUPER_ADMIN", 400);
        }
        user.setRole(role.getRoles());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public void deleteCurrentUser() {
        User user = userRepository.findByEmailAndEnabledTrue(getCurrentUserEmail()).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        user.setEnabled(false);
        user.addVersion();
        userRepository.save(user);
        refreshTokenService.deactivateAllByUserUuid(user.getUuid());
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }

    private Boolean emailUsed(String email) {
        User userEmail = userRepository.findByEmailAndEnabledTrue(email).orElse(null);
        return (userEmail != null);
    }

    public void createSuperAdminIfNotExists(String email, String password) {
        boolean exists = userRepository.existsByRoleContains(Role.SUPER_ADMIN);

        if (!exists && email != null && password != null && !emailUsed(email)) {
            User superAdmin = new User(
                    "Super",
                    "Admin",
                    email,
                    passwordEncoder.encode(password),
                    Set.of(Role.SUPER_ADMIN));

            userRepository.save(superAdmin);
            System.out.println("SUPER_ADMIN creado");
        }
    }
}
