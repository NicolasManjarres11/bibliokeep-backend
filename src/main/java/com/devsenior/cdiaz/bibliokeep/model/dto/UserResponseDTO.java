package com.devsenior.cdiaz.bibliokeep.model.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        Set<String> preferences,
        Integer annualGoal) {
}
