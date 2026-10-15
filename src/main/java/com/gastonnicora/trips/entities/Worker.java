package com.gastonnicora.trips.entities;

import com.gastonnicora.trips.enums.RoleCompany;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad que representa la relación entre un usuario y una empresa en la que
 * participa como trabajador.
 *
 * <p>
 * Contiene el usuario y la empresa asociados, los roles asignados al
 * trabajador, su estado y las fechas de creación y última actualización de la
 * relación.
 * </p>
 *
 * <p>
 * Un mismo usuario no puede tener más de una relación con la misma empresa,
 * según la restricción de unicidad definida sobre {@code user_uuid} y
 * {@code company_uuid}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Entity
@Table(
        name = "workers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_uuid", "company_uuid"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    /**
     * Identificador único de la relación entre el usuario y la empresa.
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    /**
     * Usuario asociado a la relación laboral.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    /**
     * Empresa asociada a la relación laboral.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "company_uuid", nullable = false)
    private Company company;

    /**
     * Indica si la relación del trabajador con la empresa se encuentra activa.
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Conjunto de roles asignados al trabajador dentro de la empresa.
     *
     * <p>
     * Los roles se almacenan como valores de tipo {@link String} y se cargan de
     * forma inmediata.
     * </p>
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private Set<RoleCompany> roles = new HashSet<>();

    /**
     * Fecha y hora en la que se creó la relación laboral.
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
     * Fecha y hora de la última actualización de la relación laboral.
     *
     * <p>
     * Su valor es gestionado automáticamente mediante {@link UpdateTimestamp}.
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Constructor para crear una relación entre un usuario y una empresa.
     *
     * <p>
     * Si el conjunto de roles recibido es {@code null}, se inicializa un
     * conjunto vacío.
     * </p>
     *
     * @param user    Usuario asociado a la relación laboral.
     * @param company Empresa asociada a la relación laboral.
     * @param roles   Conjunto de roles asignados al trabajador.
     */
    public Worker(User user, Company company, Set<RoleCompany> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
        this.user = user;
        this.company = company;
    }

    /**
     * Agrega un rol al conjunto de roles del trabajador.
     *
     * @param role Rol a asignar al trabajador.
     */
    public void addRole(RoleCompany role) {
        this.roles.add(role);
    }

    /**
     * Agrega varios roles al conjunto de roles del trabajador.
     *
     * @param roles Conjunto de roles a asignar al trabajador.
     */
    public void addRoles(Set<RoleCompany> roles) {
        this.roles.addAll(roles);
    }

    /**
     * Verifica si el trabajador tiene asignado un rol específico.
     *
     * @param role Rol a verificar.
     *
     * @return {@code true} si el trabajador tiene asignado el rol;
     * {@code false} en caso contrario.
     */
    public boolean hasRole(RoleCompany role) {
        return this.roles.contains(role);
    }

    /**
     * Elimina un rol del conjunto de roles del trabajador.
     *
     * @param role Rol a eliminar.
     */
    public void removeRole(RoleCompany role) {
        this.roles.remove(role);
    }
}
