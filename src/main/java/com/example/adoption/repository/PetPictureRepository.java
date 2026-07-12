package com.example.adoption.repository;

import com.example.adoption.model.PetPicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetPictureRepository extends JpaRepository<PetPicture, Long> {
}
