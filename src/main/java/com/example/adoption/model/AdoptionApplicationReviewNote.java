package com.example.adoption.model;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "adoption_application_review_notes")
public class AdoptionApplicationReviewNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_application_id", nullable = false)
    private AdoptionApplication adoptionApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 2000)
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public AdoptionApplication getAdoptionApplication() {
        return adoptionApplication;
    }

    public void setAdoptionApplication(AdoptionApplication adoptionApplication) {
        this.adoptionApplication = adoptionApplication;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
}
