package com.gastonnicora.trips.dtos.entitys;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.gastonnicora.trips.enums.Role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UserDTOs {
    
    private UUID uuid;
    private String name;
    private String lastname;
    private String email;
    private Set<Role> role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTOs(UUID uuid, String name, String lastname, String email, Set<Role> role, boolean enabled,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.uuid = uuid;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    
    

    
    

}
