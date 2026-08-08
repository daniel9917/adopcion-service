package com.example.adoption.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adoption.domain.ApplicationStatus;
import com.example.adoption.domain.UserType;
import com.example.adoption.dto.AdoptionApplicationReviewNoteCreateRequest;
import com.example.adoption.dto.AdoptionApplicationReviewNoteResponse;
import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.AdoptionApplicationReviewNote;
import com.example.adoption.model.User;
import com.example.adoption.repository.AdoptionApplicationReviewNoteRepository;
import com.example.adoption.repository.ApplicationRepository;

/**
 * AdoptionApplicationReviewNoteService
 */

@Service
public class AdoptionApplicationReviewNoteService {

    private final ApplicationRepository applicationService;
    private final AdoptionApplicationReviewNoteRepository reviewNoteRepository;

    public AdoptionApplicationReviewNoteService(ApplicationRepository applicationService,
            AdoptionApplicationReviewNoteRepository reviewNoteRepository) {
        this.applicationService = applicationService;
        this.reviewNoteRepository = reviewNoteRepository;
    }

    @Transactional
    public AdoptionApplicationReviewNoteResponse createReviewNote(Long applicationId,
            AdoptionApplicationReviewNoteCreateRequest request, User currentUser) {
        AdoptionApplication application = applicationService.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        boolean isOrganization = currentUser.getUserType() == UserType.ORGANIZATION;
        boolean isOwner = application.getUser().getId().equals(currentUser.getId());
        boolean isOpenStatus = ApplicationStatus.NEEDS_INFO.equals(application.getStatus())
                || ApplicationStatus.PENDING.equals(application.getStatus());
        if (!isOrganization && !(isOwner && isOpenStatus)) {
            throw new ReviewNoteNotAllowedException("Review note posting is not available.");
        }

        AdoptionApplicationReviewNote reviewNote = new AdoptionApplicationReviewNote();
        reviewNote.setAdoptionApplication(application);
        reviewNote.setUser(currentUser);
        reviewNote.setNote(request.note());
        AdoptionApplicationReviewNote savedReviewNote = reviewNoteRepository.save(reviewNote);

        return toResponse(savedReviewNote);
    }


    @Transactional(readOnly = true)
    public List<AdoptionApplicationReviewNoteResponse> listReviewNotes(Long applicationId, User currentUser) {
        AdoptionApplication application = applicationService.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        List<AdoptionApplicationReviewNote> reviewNotes = reviewNoteRepository.findByAdoptionApplication(application);
        return reviewNotes.stream().map(this::toResponse).toList();
    }

    private AdoptionApplicationReviewNoteResponse toResponse(AdoptionApplicationReviewNote savedReviewNote) {
        return new AdoptionApplicationReviewNoteResponse(
                savedReviewNote.getId(),
                savedReviewNote.getNote(),
                savedReviewNote.getUser().getName(),
                savedReviewNote.getCreatedAt()
        );
    }

    public static class ReviewNoteNotAllowedException extends RuntimeException {
        public ReviewNoteNotAllowedException(String message) {
            super(message);
        }
    }

}
