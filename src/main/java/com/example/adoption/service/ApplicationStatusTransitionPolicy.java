package com.example.adoption.service;

import com.example.adoption.domain.ApplicationStatus;
import com.example.adoption.domain.UserType;

import java.util.Map;
import java.util.Set;

public final class ApplicationStatusTransitionPolicy {
    private static final Map<UserType, Map<ApplicationStatus, Set<ApplicationStatus>>> ALLOWED_TRANSITIONS = Map.of(
            UserType.REGULAR, Map.of(
                    ApplicationStatus.NEEDS_INFO, Set.of(ApplicationStatus.PENDING)
            ),
            UserType.ORGANIZATION, Map.of(
                    ApplicationStatus.PENDING, Set.of(ApplicationStatus.NEEDS_INFO, ApplicationStatus.APPROVED, ApplicationStatus.REJECTED),
                    ApplicationStatus.APPROVED, Set.of(ApplicationStatus.REJECTED, ApplicationStatus.COMPLETED)
            )
    );

    private ApplicationStatusTransitionPolicy() {
    }

    public static boolean isAllowed(UserType userType, ApplicationStatus current, ApplicationStatus target) {
        Set<ApplicationStatus> allowedTargets = ALLOWED_TRANSITIONS
                .getOrDefault(userType, Map.of())
                .get(current);
        return allowedTargets != null && allowedTargets.contains(target);
    }
}
