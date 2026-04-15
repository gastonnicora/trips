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
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenService) {
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
        User newUser = new User(null,
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                Set.of(Role.USER),
                true,
                null,
                null);

        return userMapper.toDTO(userRepository.save(newUser));
    }

    public UserDTOs getCurrentUser() {
        String email = getCurrentUserEmail();
        Optional<User> user = userRepository.findByEmailAndEnabled(email, true);
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

    public UserDTOs putUserByUuid(UUID uuid, UserPut user) {
        User userNow = userRepository.findByUuid(uuid).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        String email = userNow.getEmail();
        if (!user.getEmail().equals(userNow.getEmail())) {
            if (emailUsed(user.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");
                throw ex;
            }
            userNow.setEmail(user.getEmail());

            refreshTokenService.deactivateAllByEmail(email);
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);

    }

    public UserDTOs putCurrentUser(UserPut user) {
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(), true).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        String email = userNow.getEmail();
        if (!user.getEmail().equals(userNow.getEmail())) {
            if (emailUsed(user.getEmail())) {
                ErrorException ex = new ErrorException("Error en la validación", 400);
                ex.addError("email", "El email ya esta en uso");

                throw ex;
            }
            userNow.setEmail(user.getEmail());
            refreshTokenService.deactivateAllByEmail(email);
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);

    }

    public UserDTOs updatePassword(UserPassword user) {
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(), true).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        if (!passwordEncoder.matches(user.getPasswordOld(), userNow.getPassword())) {
            ErrorException ex = new ErrorException("Error en la validación", 400);
            ex.addError("passwordOld", "La contraseña actual es incorrecta");

            throw ex;
        }
        userNow.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userNow);
        refreshTokenService.deactivateAllByEmail(getCurrentUserEmail());

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

    public void deleteCurrentUser() {
        User user = userRepository.findByEmailAndEnabled(getCurrentUserEmail(), true).orElseThrow(
                () -> new ErrorException("El usuario no existe", 400));
        user.setEnabled(false);
        userRepository.save(user);
        refreshTokenService.deactivateAllByEmail(user.getEmail());
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }

    private Boolean emailUsed(String email) {
        User userEmail = userRepository.findByEmailAndEnabled(email, true).orElse(null);
        return (userEmail != null);
    }
}
