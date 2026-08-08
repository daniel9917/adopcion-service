package com.example.adoption.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.AdoptionApplicationReviewNote;

public interface AdoptionApplicationReviewNoteRepository extends JpaRepository<AdoptionApplicationReviewNote, Long> {

    List<AdoptionApplicationReviewNote> findByAdoptionApplication(AdoptionApplication application);
    
}
