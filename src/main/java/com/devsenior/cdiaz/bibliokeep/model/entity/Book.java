package com.devsenior.cdiaz.bibliokeep.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books", indexes = @Index(name = "idx_isbn", columnList = "isbn", unique = true))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    @NotNull(message = "El ID del propietario es obligatorio")
    private UUID ownerId;

    @Column(nullable = false, unique = true, length = 13)
    @NotBlank(message = "El ISBN es obligatorio")
    @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "El ISBN debe tener 10 o 13 dígitos")
    private String isbn;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    private String title;

    @ElementCollection
    @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author")
    @Builder.Default
    private List<String> authors = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private BookStatus status;

    @Column
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @Column(nullable = false)
    @NotNull(message = "El campo isLent es obligatorio")
    @Builder.Default
    private Boolean isLent = false;
}
