package com.devsenior.cdiaz.bibliokeep.model.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = @Index(name = "idx_email", columnList = "email", unique = true))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false, unique = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @ElementCollection
    @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preference")
    @Builder.Default
    private Set<String> preferences = new HashSet<>();

    @Column(nullable = false)
    @NotNull(message = "La meta anual es obligatoria")
    @Min(value = 1, message = "La meta anual debe ser al menos 1")
    @Builder.Default
    private Integer annualGoal = 12;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}
