package com.gastonnicora.trips.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

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
 * Contiene información personal, roles, estado y fechas de
 * creación/actualización.
 * Por defecto, se asigna el rol {@link Role#USER}.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del usuario.</li>
 * <li>{@code name}: Nombre del usuario.</li>
 * <li>{@code lastname}: Apellido del usuario.</li>
 * <li>{@code email}: Correo electrónico del usuario.</li>
 * <li>{@code password}: Contraseña cifrada del usuario.</li>
 * <li>{@code role}: Conjunto de roles asignados al usuario.</li>
 * <li>{@code enabled}: Indica si el usuario está habilitado.</li>
 * <li>{@code createdAt}: Fecha y hora de creación.</li>
 * <li>{@code updatedAt}: Fecha y hora de última actualización.</li>
 * <li>{@code version}: Versión del usuario.</li>
 * </ul>
 * <p>
 * Se utiliza para gestionar la autenticación, autorización y administración de
 * usuarios.
 * </p>
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-05-04
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class User {

     @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    private int version = 0;

    /**
     * Constructor sin roles.
     * <p>
     * Asigna automáticamente el rol {@link Role#USER}.
     * </p>
     * 
     * @param name     Nombre del usuario
     * @param lastname Apellido del usuario
     * @param email    Correo electrónico
     * @param password Contraseña cifrada
     */
    public User(String name, String lastname, String email, String password) {
        this(name, lastname, email, password, null);
    }

    /**
     * Constructor con roles.
     * <p>
     * Si no se proporcionan roles, se asigna automáticamente {@link Role#USER}.
     * </p>
     * 
     * @param name     Nombre del usuario
     * @param lastname Apellido del usuario
     * @param email    Correo electrónico
     * @param password Contraseña cifrada
     * @param role     ({@link Set}) Conjunto de {@link Role} a asignar
     */
    public User(String name, String lastname, String email, String password, Set<Role> role) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = (role != null) ? new HashSet<>(role) : new HashSet<>();
        this.role.add(Role.USER);
    }

    /**
     * Agrega un rol al usuario.
     * 
     * @param role ({@link Role}) a asignar
     */
    public void addRole(Role role) {
        this.role.add(role);
    }

    /**
     * Agrega varios roles al usuario.
     * 
     * @param roles ({@link Set}) Conjunto de {@link Role} a asignar
     */
    public void addRoles(Set<Role> roles) {
        this.role.addAll(roles);
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     * 
     * @param role ({@link Role})) Rol a verificar
     * @return {@code true} si el usuario tiene el rol, {@code false} en caso
     *         contrario
     */
    public boolean hasRole(Role role) {
        return this.role.contains(role);
    }

    /**
     * Elimina un rol del usuario.
     * 
     * @param role ({@link Role}) a eliminar
     */
    public void removeRole(Role role) {
        this.role.remove(role);
    }

    /**
     * Incrementa la versión del usuario.
     */
    public void addVersion() {
        this.version++;
    }

}