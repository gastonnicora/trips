package com.gastonnicora.trips.entities;

import com.gastonnicora.trips.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.BatchSize;

/**
 * Entidad que representa un usuario en el sistema.
 *
 * <p>
 * Contiene la información personal del usuario, sus credenciales, los roles
 * asignados, su estado y las fechas de creación y última actualización.
 * </p>
 *
 * <p>
 * Cuando se crea un usuario mediante los constructores disponibles, se asigna
 * automáticamente el rol {@link Role#USER}.
 * </p>
 *
 * <p>
 * La contraseña se excluye de la representación generada mediante
 * {@link ToString}.
 * </p>
 *
 * <p>
 * Se utiliza para la gestión de autenticación, autorización y administración de
 * usuarios.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-04-21
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email_enabled", columnList = "email, enabled")
})
public class User {

    /**
     * Identificador único del usuario.
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
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
     * Contraseña cifrada del usuario.
     *
     * <p>
     * Este atributo se excluye de la representación generada mediante
     * {@link ToString}.
     * </p>
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Conjunto de roles asignados al usuario.
     *
     * <p>
     * Los roles se almacenan como valores de tipo {@link String} y se cargan
     * de forma inmediata.
     * </p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> role;

    /**
     * Indica si el usuario se encuentra habilitado.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    /**
     * Fecha y hora en la que se creó el usuario.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante
     * {@link CreationTimestamp}.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización del usuario.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link UpdateTimestamp}.
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Versión actual del usuario.
     */
    @Column(name = "version", nullable = false)
    private int version = 0;

    /**
     * Constructor para crear un usuario sin especificar roles.
     *
     * <p>
     * Asigna automáticamente el rol {@link Role#USER}.
     * </p>
     *
     * @param name     Nombre del usuario.
     * @param lastname Apellido del usuario.
     * @param email    Dirección de correo electrónico del usuario.
     * @param password Contraseña cifrada del usuario.
     */
    public User(String name, String lastname, String email, String password) {
        this(name, lastname, email, password, null);
    }

    /**
     * Constructor para crear un usuario con los roles especificados.
     *
     * <p>
     * Si el conjunto de roles recibido es {@code null}, se inicializa un
     * conjunto vacío. En todos los casos se agrega automáticamente el rol
     * {@link Role#USER}.
     * </p>
     *
     * @param name     Nombre del usuario.
     * @param lastname Apellido del usuario.
     * @param email    Dirección de correo electrónico del usuario.
     * @param password Contraseña cifrada del usuario.
     * @param role     Conjunto de roles a asignar al usuario.
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
     * Agrega un rol al conjunto de roles del usuario.
     *
     * @param role Rol a asignar al usuario.
     */
    public void addRole(Role role) {
        this.role.add(role);
    }

    /**
     * Agrega varios roles al conjunto de roles del usuario.
     *
     * @param roles Conjunto de roles a asignar al usuario.
     */
    public void addRoles(Set<Role> roles) {
        this.role.addAll(roles);
    }

    /**
     * Incrementa en uno la versión actual del usuario.
     */
    public void addVersion() {
        this.version++;
    }

    /**
     * Verifica si el usuario tiene asignado un rol específico.
     *
     * @param role Rol a verificar.
     *
     * @return {@code true} si el usuario tiene asignado el rol; {@code false}
     * en caso contrario.
     */
    public boolean hasRole(Role role) {
        return this.role.contains(role);
    }

    /**
     * Elimina un rol del conjunto de roles del usuario.
     *
     * @param role Rol a eliminar.
     */
    public void removeRole(Role role) {
        this.role.remove(role);
    }

}
