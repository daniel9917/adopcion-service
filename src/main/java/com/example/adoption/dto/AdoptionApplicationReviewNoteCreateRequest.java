package com.example.adoption.dto;

import jakarta.validation.constraints.NotBlank;

public record AdoptionApplicationReviewNoteCreateRequest(
    @NotBlank String note
) {
}
