package com.example.adoption.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationCreateRequest(
        @NotNull Long petId,
        @NotBlank String applicantName,
        @NotBlank @Email String applicantEmail,
        @NotBlank String applicantPhone,
        String message
) {
}
