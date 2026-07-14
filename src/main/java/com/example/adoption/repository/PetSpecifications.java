package com.example.adoption.repository;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import com.example.adoption.model.Pet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PetSpecifications {

    private PetSpecifications() {}

    public static Specification<Pet> withFilters(Species species, Breed breed,
                                                 Integer minAgeMonths, Integer maxAgeMonths,
                                                 PetStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (species != null) {
                predicates.add(cb.equal(root.get("species"), species));
            }
            if (breed != null) {
                predicates.add(cb.equal(root.get("breed"), breed));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minAgeMonths != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ageMonths"), minAgeMonths));
            }
            if (maxAgeMonths != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ageMonths"), maxAgeMonths));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
