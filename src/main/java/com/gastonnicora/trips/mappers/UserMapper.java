package com.gastonnicora.trips.mappers;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entitys.UserDTOs;
import com.gastonnicora.trips.entitys.User;

@Component
public class UserMapper {
    public  UserDTOs toDTO(User user) {
        return new UserDTOs(
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
    public User toEntity(UserDTOs userDTO) {
       return new User(
                userDTO.getUuid(),
                userDTO.getName(),
                userDTO.getLastname(),
                userDTO.getEmail(),
                null,
                userDTO.getRole(),
                userDTO.isEnabled(),
                null,
                null
        );
    }
    public List<UserDTOs> toDTOList(List<User> users) {
        return users.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
    }
    public List<User> toEntityList(List<UserDTOs> userDTOs) {
        return userDTOs.stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList());
    }

}
