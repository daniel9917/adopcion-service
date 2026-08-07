package com.example.adoption.controller;

import com.example.adoption.dto.UserCreateRequest;
import com.example.adoption.dto.UserResponse;
import com.example.adoption.model.User;
import com.example.adoption.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId, Authentication authentication) {
        return userService.getUserResponse(userId, (User) authentication.getPrincipal());
    }
}
