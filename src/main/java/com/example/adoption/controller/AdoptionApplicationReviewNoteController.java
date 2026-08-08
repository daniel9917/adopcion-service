package com.example.adoption.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.adoption.dto.AdoptionApplicationReviewNoteCreateRequest;
import com.example.adoption.dto.AdoptionApplicationReviewNoteResponse;
import com.example.adoption.model.User;
import com.example.adoption.service.AdoptionApplicationReviewNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/applications/{applicationId}/review-notes")
public class AdoptionApplicationReviewNoteController {

    private final AdoptionApplicationReviewNoteService reviewNoteService;
    public AdoptionApplicationReviewNoteController(AdoptionApplicationReviewNoteService reviewNoteService) {
        this.reviewNoteService = reviewNoteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionApplicationReviewNoteResponse createReviewNote(
            @PathVariable Long applicationId,
            @Valid @RequestBody AdoptionApplicationReviewNoteCreateRequest request,
            Authentication authentication) {
        return reviewNoteService.createReviewNote(applicationId, request, (User) authentication.getPrincipal());
    }

    @GetMapping
    public java.util.List<AdoptionApplicationReviewNoteResponse> listReviewNotes(
            @PathVariable Long applicationId,
            Authentication authentication) {
        return reviewNoteService.listReviewNotes(applicationId, (User) authentication.getPrincipal());
    }
    
}
