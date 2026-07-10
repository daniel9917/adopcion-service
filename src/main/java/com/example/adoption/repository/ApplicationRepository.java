package com.example.adoption.repository;

import com.example.adoption.model.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
}
