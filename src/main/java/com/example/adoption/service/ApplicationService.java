package com.example.adoption.service;

import com.example.adoption.domain.ApplicationStatus;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.UserType;
import com.example.adoption.dto.ApplicationCreateRequest;
import com.example.adoption.dto.ApplicationResponse;
import com.example.adoption.dto.ApplicationUpdateRequest;
import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.Pet;
import com.example.adoption.model.User;
import com.example.adoption.repository.ApplicationRepository;
import com.example.adoption.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {
    private static final int MAX_PAGE_SIZE = 100;

    private final ApplicationRepository applicationRepository;
    private final PetRepository petRepository;

    public ApplicationService(ApplicationRepository applicationRepository, PetRepository petRepository) {
        this.applicationRepository = applicationRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public ApplicationResponse createApplication(ApplicationCreateRequest request, User applicant) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new IllegalStateException("Pet is not available for adoption");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setPet(pet);
        application.setUser(applicant);
        application.setApplicantName(request.applicantName());
        application.setApplicantEmail(request.applicantEmail());
        application.setApplicantPhone(request.applicantPhone());
        application.setMessage(request.message());
        application.setStatus(ApplicationStatus.PENDING);
        AdoptionApplication savedApplication = applicationRepository.save(application);

        return toResponse(savedApplication);
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> listApplications(User currentUser, Pageable pageable) {
        Pageable bounded = pageable.getPageSize() > MAX_PAGE_SIZE
                ? PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort())
                : pageable;
        Page<AdoptionApplication> applications = (currentUser.getUserType() == UserType.ORGANIZATION)
                ? applicationRepository.findAll(bounded)
                : applicationRepository.findByUser(currentUser, bounded);
        return applications.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(Long applicationId, User currentUser) {
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (currentUser.getUserType() != UserType.ORGANIZATION && !application.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Application does not belong to the current user");
        }
        return toResponse(application);
    }

    @Transactional
    public ApplicationResponse updateApplication(Long applicationId, ApplicationUpdateRequest request) {
        AdoptionApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        application.setStatus(request.status());
        application.setReviewNotes(request.reviewNotes());
        AdoptionApplication savedApplication = applicationRepository.save(application);
        return toResponse(savedApplication);
    }

    private ApplicationResponse toResponse(AdoptionApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getUser().getId(),
                application.getPet().getId(),
                application.getApplicantName(),
                application.getApplicantEmail(),
                application.getApplicantPhone(),
                application.getMessage(),
                application.getStatus().name(),
                application.getCreatedAt(),
                application.getUpdatedAt());
    }

    public static class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}
