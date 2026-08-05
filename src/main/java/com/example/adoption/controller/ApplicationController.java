package com.example.adoption.controller;

import com.example.adoption.dto.ApplicationCreateRequest;
import com.example.adoption.dto.ApplicationResponse;
import com.example.adoption.dto.ApplicationUpdateRequest;
import com.example.adoption.model.User;
import com.example.adoption.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request, Authentication authentication) {
        return applicationService.createApplication(request, (User) authentication.getPrincipal());
    }

    @GetMapping
    public Page<ApplicationResponse> listApplications(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return applicationService.listApplications((User) authentication.getPrincipal(), pageable);
    }

    @GetMapping("/{applicationId}")
    public ApplicationResponse getApplication(@PathVariable Long applicationId, Authentication authentication) {
        return applicationService.getApplication(applicationId, (User) authentication.getPrincipal());
    }

    @PatchMapping("/{applicationId}")
    public ApplicationResponse updateApplication(@PathVariable Long applicationId, @Valid @RequestBody ApplicationUpdateRequest request) {
        return applicationService.updateApplication(applicationId, request);
    }
}
