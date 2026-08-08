package com.example.adoption.dto;

import com.example.adoption.domain.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record ApplicationPatchRequest(
        @NotNull ApplicationStatus status,
        Optional<String> applicantName,
        Optional<String> applicantEmail,
        Optional<String> applicantPhone,
        Optional<String> message
) {
}
