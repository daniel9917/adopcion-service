package com.example.adoption.service;

import com.example.adoption.dto.UserCreateRequest;
import com.example.adoption.dto.UserResponse;
import com.example.adoption.model.User;
import com.example.adoption.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUserType(),
                savedUser.getName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getCity(),
                savedUser.getPhoneNumber(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt());
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
