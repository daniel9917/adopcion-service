package com.example.adoption.dto;

import java.time.Instant;

public record ApplicationResponse(
        Long applicationId,
        Long petId,
        String applicantName,
        String applicantEmail,
        String applicantPhone,
        String message,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}