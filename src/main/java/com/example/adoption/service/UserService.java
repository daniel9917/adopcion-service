package com.example.adoption.service;

import com.example.adoption.domain.UserType;
import com.example.adoption.dto.UserCreateRequest;
import com.example.adoption.dto.UserResponse;
import com.example.adoption.model.User;
import com.example.adoption.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();
        user.setUserType(request.userType());
        user.setName(request.name());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCity(request.city());
        user.setPhoneNumber(request.phoneNumber());
        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponse(Long userId, User currentUser) {
        User user = getUserOrThrow(userId);
        if (currentUser.getUserType() != UserType.ORGANIZATION && !user.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this user");
        }
        return toResponse(user);
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserType(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getCity(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
