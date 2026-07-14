package com.example.adoption.service;

import com.example.adoption.domain.ApplicationStatus;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.dto.ApplicationCreateRequest;
import com.example.adoption.dto.ApplicationResponse;
import com.example.adoption.dto.ApplicationUpdateRequest;
import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.Pet;
import com.example.adoption.repository.ApplicationRepository;
import com.example.adoption.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final PetRepository petRepository;

    public ApplicationService(ApplicationRepository applicationRepository, PetRepository petRepository) {
        this.applicationRepository = applicationRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalStateException("Pet is not available for adoption");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setPet(pet);
        application.setApplicantName(request.applicantName());
        application.setApplicantEmail(request.applicantEmail());
        application.setApplicantPhone(request.applicantPhone());
        application.setMessage(request.message());
        application.setStatus(ApplicationStatus.PENDING);
        AdoptionApplication savedApplication = applicationRepository.save(application);

        return new ApplicationResponse(
                savedApplication.getId(),
                savedApplication.getPet().getId(),
                savedApplication.getApplicantName(),
                savedApplication.getApplicantEmail(),
                savedApplication.getApplicantPhone(),
                savedApplication.getMessage(),
                savedApplication.getStatus().name(),
                savedApplication.getCreatedAt(),
                savedApplication.getUpdatedAt());

    }

    @Transactional
    public ApplicationResponse updateApplication(Long applicationId, ApplicationUpdateRequest request) {
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.setStatus(request.status());
        application.setReviewNotes(request.reviewNotes());
        AdoptionApplication savedApplication = applicationRepository.save(application);
        return new ApplicationResponse(
                savedApplication.getId(),
                savedApplication.getPet().getId(),
                savedApplication.getApplicantName(),
                savedApplication.getApplicantEmail(),
                savedApplication.getApplicantPhone(),
                savedApplication.getMessage(),
                savedApplication.getStatus().name(),
                savedApplication.getCreatedAt(),
                savedApplication.getUpdatedAt());
    }
}
