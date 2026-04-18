package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Schema(description = "DTO de usuario para Request")
public abstract class UserBasic {

    
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    @Size(max = 255, message = "El nombre no puede tener mas de 255 caracteres")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Perez")
    @NotBlank(message = "El apellido no puede quedar en blanco")
    @Size(max = 255, message = "El apellido no puede tener mas de 255 caracteres")
    private String lastname;

    @Schema(description = "Email del usuario",example = "juanperez@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Email(message = "El email no es valido")
    @Size(max = 255, message = "El email no puede tener mas de 255 caracteres")
    private String email;


    public UserBasic(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

}

