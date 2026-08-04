package com.example.adoption.security;

import com.example.adoption.domain.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class UserRoleMapper {
    private UserRoleMapper() {
    }

    public static String toRole(UserType userType) {
        return "ROLE_" + userType.name();
    }

    public static List<GrantedAuthority> toAuthorities(UserType userType) {
        return List.of(new SimpleGrantedAuthority(toRole(userType)));
    }

    public static UserType fromRole(String role) {
        if (role == null || !role.startsWith("ROLE_")) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        return UserType.valueOf(role.substring("ROLE_".length()));
    }
}
