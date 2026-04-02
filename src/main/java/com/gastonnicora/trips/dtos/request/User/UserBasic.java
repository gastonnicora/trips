package com.gastonnicora.trips.dtos.request.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//TODO Agregar mensajes si falta y Size
//TODO Crear clase que devuelva los mensajes de cada error

public abstract class UserBasic {

    
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre no puede quedar en blanco")
    private String name;

    @Schema(description = "Apellido del usuario", example = "Perez")
    @NotBlank(message = "El apellido no puede quedar en blanco")
    private String lastname;

    @Schema(description = "Email valido",example = "juanperez@mail.com")
    @NotBlank(message = "El email no puede quedar en blanco")
    @Email(message = "Debe introducir un email valido")
    private String email;


    public UserBasic(String name, String lastname, String email) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
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
        this.email = email;
    }

}

