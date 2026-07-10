package com.example.adoption.dto;

import com.example.adoption.domain.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationUpdateRequest(
        @NotNull ApplicationStatus status,
        String reviewNotes
) {
}
