package com.example.adoption.model;

import com.example.adoption.domain.ApplicationStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "applications")
public class AdoptionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "applicant_phone", nullable = false)
    private String applicantPhone;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "review_notes", length = 2000)
    private String reviewNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }
    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
