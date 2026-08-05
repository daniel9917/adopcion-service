package com.example.adoption.repository;

import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    Page<AdoptionApplication> findByUser(User user, Pageable pageable);
}
