package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Componente encargado de convertir entidades {@link User} en objetos
 * {@link UserDTO}.
 *
 * <p>
 * Proporciona métodos para transformar una entidad individual o una lista de
 * entidades en los DTOs correspondientes.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-03-18
 */
@Component
public class UserMapper {

    /**
     * Convierte una entidad {@link User} en un {@link UserDTO}.
     *
     * <p>
     * La conversión incluye los datos de identificación, información del usuario,
     * rol, estado y fechas de creación y actualización.
     * </p>
     *
     * @param user Entidad de usuario que se desea convertir.
     *
     * @return DTO de usuario correspondiente a la entidad proporcionada.
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
                user.getUpdatedAt()
        );
    }

    /**
     * Convierte una lista de entidades {@link User} en una lista de
     * {@link UserDTO}.
     *
     * @param users Lista de entidades de usuarios que se desea convertir.
     *
     * @return Lista de DTOs de usuarios correspondientes a las entidades
     * proporcionadas.
     */
    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

}
