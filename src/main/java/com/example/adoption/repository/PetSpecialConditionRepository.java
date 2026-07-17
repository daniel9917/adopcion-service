package com.example.adoption.repository;

import com.example.adoption.model.PetSpecialCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSpecialConditionRepository extends JpaRepository<PetSpecialCondition, Long> {
    
}
