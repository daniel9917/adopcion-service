package com.example.adoption.repository;

import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    List<AdoptionApplication> findByUser(User user);
}
