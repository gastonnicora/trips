package com.gastonnicora.trips.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.gastonnicora.trips.enums.RoleCompany;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un trabajador en una empresa.
 * <p>
 * Contiene información personal, roles, estado y fechas de
 * creación/actualización.
 * Por defecto, se asigna el rol {@link RoleCompany#SELLER}.
 * </p>
 * <p>
 * Campos principales:
 * </p>
 * <ul>
 * <li>{@code uuid}: Identificador único del trabajador.</li>
 * <li>{@code user}: Usuario asociado.</li>
 * <li>{@code company}: Empresa asociada.</li>
 * <li>{@code active}: Indica si el trabajador está activo.</li>
 * <li>{@code role}: Conjunto de roles asignados al trabajador.</li>
 * <li>{@code createdAt}: Fecha y hora de creación.</li>
 * <li>{@code updatedAt}: Fecha y hora de última actualización.</li>
 * </ul>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Entity
@Table(name = "workers", uniqueConstraints = @UniqueConstraint(columnNames = { "user_uuid", "company_uuid" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    @GeneratedValue
    @UuidGenerator
    private UUID uuid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_uuid", nullable = false)
    private Company company;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private Set<RoleCompany> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Constructor con los datos necesarios.
     *
     * @param user    ({@link User}) Usuario asociado
     * @param company ({@link Company}) Empresa asociada
     * @param roles   ({@link Set}) Conjunto de {@link RoleCompany} a asignar
     */
    public Worker(User user, Company company, Set<RoleCompany> roles) {
        this.roles = (roles != null) ? new HashSet<>(roles) : new HashSet<>();
        this.user = user;
        this.company = company;
    }

    /**
     * Método para agregar un rol al trabajador.
     * 
     * @param role ({@link RoleCompany}) Rol a agregar
     */
    public void addRole(RoleCompany role) {
        this.roles.add(role);
    }

    /**
     * Método para agregar varios roles al trabajador.
     * 
     * @param roles ({@link Set}) Conjunto de roles a agregar
     */
    public void addRoles(Set<RoleCompany> roles) {
        this.roles.addAll(roles);
    }

    /**
     * Método para verificar si el trabajador tiene un rol específico.
     * 
     * @param role ({@link RoleCompany}) Rol a verificar
     * @return {@code true} si el trabajador tiene el rol, {@code false} en caso
     *         contrario
     */
    public boolean hasRole(RoleCompany role) {
        return this.roles.contains(role);
    }

    /**
     * Método para eliminar un rol del trabajador.
     * 
     * @param role ({@link RoleCompany}) Rol a eliminar
     */
    public void removeRole(RoleCompany role) {
        this.roles.remove(role);
    }
}
