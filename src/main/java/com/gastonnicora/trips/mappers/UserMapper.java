package com.gastonnicora.trips.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.UserDTOs;
import com.gastonnicora.trips.entities.User;

@Component
public class UserMapper {
    public UserDTOs toDTO(User user) {
        return new UserDTOs(
                user.getUuid(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

  

    public List<UserDTOs> toDTOList(List<User> users) {
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

  
}
