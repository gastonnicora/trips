package com.gastonnicora.trips.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.gastonnicora.trips.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa un usuario en el sistema.
 * <p>
 * Esta clase contiene información personal del usuario, sus roles, estado y las
 * fechas de creación y actualización.
 * Además, se gestionan las funciones básicas de los usuarios, como la
 * asignación y eliminación de roles.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2023-05-04
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class User {

    /**
     * UUID único para identificar al usuario en el sistema.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    /**
     * Nombre del usuario.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Apellido del usuario.
     */
    @Column(name = "lastname", nullable = false)
    private String lastname;

    /**
     * Dirección de correo electrónico del usuario.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Contraseña del usuario.
     */
    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    private int version = 0;

    public User(String name, String lastname, String email, String password) {
        this(name, lastname, email, password, null);
    }

    public User(String name, String lastname, String email, String password, Set<Role> role) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = (role != null) ? new HashSet<>(role) : new HashSet<>();
        this.role.add(Role.USER);

    }

    /** 
     * @param role
     */
    public void addRole(Role role) {
        this.role.add(role);
    }

    /** 
     * @param roles
     */
    public void addRoles(Set<Role> roles) {
        this.role.addAll(roles);
    }

    /** 
     * @param role
     * @return boolean
     */
    public boolean hasRole(Role role) {
        return this.role.contains(role);
    }

    /** 
     * @param role
     */
    public void removeRole(Role role) {
        this.role.remove(role);
    }

    public void addVersion() {
        this.version++;
    }

}
