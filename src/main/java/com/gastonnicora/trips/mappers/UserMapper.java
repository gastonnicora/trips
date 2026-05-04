package com.gastonnicora.trips.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.UserDTO;
import com.gastonnicora.trips.entities.User;

@Component
public class UserMapper {
    /** 
     * @param user
     * @return UserDTO
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
     * @param users
     * @return List
     */
    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

  
}
