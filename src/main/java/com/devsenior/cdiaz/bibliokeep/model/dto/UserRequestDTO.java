package com.devsenior.cdiaz.bibliokeep.model.dto;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        
        
        @NotBlank(message = "La contraseña es obligatoria")
        String password,
        
        Set<String> preferences,
        
        @NotNull(message = "La meta anual es obligatoria")
        @Min(value = 1, message = "La meta anual debe ser al menos 1")
        Integer annualGoal,

        List<String> roles
) {
}
