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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserMapper userMapper;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTOs createUser(UserCreate user) {
        if (emailUsed(user.getEmail())) {
            throw new ErrorException("El email ya esta siendo utilizado",400);
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
        Optional<User> user = userRepository.findByEmailAndEnabled(email,true);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        throw new ErrorException("El usuario no se encontró",400);

    }

    public UserDTOs getUserByUuid(UUID uuid) {
        Optional<User> user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        throw new ErrorException("El usuario buscado no existe",400);

    }

    public UserDTOs putUserByUuid(UUID uuid, UserPut user) {
        User userNow = userRepository.findByUuid(uuid).orElseThrow(
            ()-> 
            new ErrorException("El usuario no existe",400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        if (!user.getEmail().equals(userNow.getEmail())) {
            if(emailUsed(user.getEmail())){
                throw new ErrorException("El email ya esta en uso",400);
            }
            userNow.setEmail(user.getEmail());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);   

    }

    
    //TODO corregir accion al cambiar el email
    public UserDTOs putCurrentUser(UserPut user) {
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(),true).orElseThrow(
            ()-> 
            new ErrorException("El usuario no existe",400));
        userNow.setName(user.getName());
        userNow.setLastname(user.getLastname());
        System.err.println("EMAIL "+user.getEmail());
        System.err.println("EMAIL VIEJO"+userNow.getEmail());
        if (!user.getEmail().equals(userNow.getEmail())) {
            System.err.println("EMAIL DISTINTO");
            if(emailUsed(user.getEmail())){
                log.error("EMAIL EN USO");
                log.error(user.getEmail());
                throw new ErrorException("El email ya esta en uso",400);
            }
            userNow.setEmail(user.getEmail());
        }
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);   

    }

    public UserDTOs updatePassword(UserPassword user){
        log.error("contraseña anterior "+user.getPasswordOld());
        log.error("contraseña nueva "+user.getPassword());
        log.error("contraseña nueva "+user.getConfirmPassword());
        User userNow = userRepository.findByEmailAndEnabled(getCurrentUserEmail(), true).orElseThrow(
            ()-> 
            new ErrorException("El usuario no existe", 400)
        );
        log.error("el usuario exxiste");
        if (!passwordEncoder.matches(user.getPasswordOld(), userNow.getPassword())) {
            log.error("contraseña incorrecta");
            throw new ErrorException("La contraseña actual es incorrecta",400);
        }
        log.error("contraseña correcta");
        userNow.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userNow);
        return userMapper.toDTO(userNow);
    }

    public UserDTOs setRole(UUID uuid, UserRole role){
        User user = userRepository.findByUuid(uuid).orElseThrow(
            ()-> 
            new ErrorException("El usuario no existe",400)
        );
        if (!user.getRole().contains(Role.SUPER_ADMIN)){
            role.getRoles().remove(Role.SUPER_ADMIN);
        }else{
            throw new ErrorException("No se puede modificar los roles el SUPER_ADMIN",400);
        }
        user.setRole(role.getRoles());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }


    public void deleteCurrentUser(){
        User user= userRepository.findByEmailAndEnabled(getCurrentUserEmail(),true).orElseThrow(
            ()-> 
            new ErrorException("El usuario no existe",400)
        );
        user.setEnabled(false);
        userRepository.save(user);
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }

    private Boolean emailUsed(String email) {
        User userEmail = userRepository.findByEmailAndEnabled(email,true).orElse(null);
        return (userEmail != null );
    }
}
