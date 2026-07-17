package com.example.adoption.repository;

import com.example.adoption.domain.Breed;
import com.example.adoption.domain.PetSex;
import com.example.adoption.domain.PetStatus;
import com.example.adoption.domain.Species;
import com.example.adoption.model.Pet;
import com.example.adoption.model.PetSpecialCondition;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PetSpecifications {

    private PetSpecifications() {
    }

    public static Specification<Pet> withFilters(Species species, Breed breed, PetSex sex,
            Integer minAgeMonths, Integer maxAgeMonths,
            PetStatus status, Boolean hasSpecialConditions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (species != null) {
                predicates.add(cb.equal(root.get("species"), species));
            }
            if (breed != null) {
                predicates.add(cb.equal(root.get("breed"), breed));
            }
            if (sex != null) {
                predicates.add(cb.equal(root.get("sex"), sex));
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
            if (hasSpecialConditions != null) {
                Subquery<PetSpecialCondition> subquery = query.subquery(PetSpecialCondition.class);
                Root<PetSpecialCondition> child = subquery.from(PetSpecialCondition.class);

                subquery.select(child)
                        .where(
                            cb.equal(child.get("petId"), root.get("id")
                        )
                );
                predicates.add(
                    hasSpecialConditions ? cb.exists(subquery) : cb.not(cb.exists(subquery)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
