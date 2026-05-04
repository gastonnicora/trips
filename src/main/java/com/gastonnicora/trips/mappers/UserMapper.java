package com.gastonnicora.trips.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.entities.User;

/**
 * Mapper que convierte entidades {@link User} a {@link UserDTO}.
 * <p>
 * Se utiliza para exponer datos de usuario de manera segura en la API,
 * sin incluir información sensible como la contraseña.
 * </p>
 */
@Component
public class UserMapper {

    /**
     * Convierte un {@link User} en {@link UserDTO}.
     * 
     * @param user Entidad de usuario
     * @return DTO de usuario correspondiente
     */
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUuid(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    /**
     * Convierte una lista de {@link User} en una lista de {@link UserDTO}.
     * 
     * @param users Lista de entidades de usuario
     * @return Lista de DTOs de usuario correspondientes
     */
    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}