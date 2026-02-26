package com.devsenior.cdiaz.bibliokeep.model.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

import com.devsenior.cdiaz.bibliokeep.model.entity.BookStatus;

public record BookRequestDTO(
        UUID ownerId,
        
        @NotBlank(message = "El ISBN es obligatorio")
        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "El ISBN debe tener 10 o 13 dígitos")
        String isbn,
        
        @NotBlank(message = "El título es obligatorio")
        String title,
        
        List<String> authors,
        
        String description,
        
        String thumbnail,
        
        @NotNull(message = "El estado es obligatorio")
        BookStatus status,
        
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer rating,
        
        Boolean isLent
) {
}
