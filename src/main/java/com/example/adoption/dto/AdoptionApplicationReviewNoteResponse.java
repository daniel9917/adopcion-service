package com.example.adoption.dto;

import java.time.Instant;

public record AdoptionApplicationReviewNoteResponse(
    Long id,
    String note,
    String createdBy,
    Instant createdAt
) {

}
