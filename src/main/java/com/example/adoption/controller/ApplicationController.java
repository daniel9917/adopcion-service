package com.example.adoption.controller;

import com.example.adoption.dto.ApplicationCreateRequest;
import com.example.adoption.dto.ApplicationUpdateRequest;
import com.example.adoption.model.AdoptionApplication;
import com.example.adoption.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public AdoptionApplication createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        return applicationService.createApplication(request);
    }

    @PatchMapping("/{applicationId}")
    public AdoptionApplication updateApplication(@PathVariable Long applicationId, @Valid @RequestBody ApplicationUpdateRequest request) {
        return applicationService.updateApplication(applicationId, request);
    }
}
