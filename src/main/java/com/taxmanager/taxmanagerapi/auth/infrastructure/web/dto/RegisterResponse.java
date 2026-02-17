package com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto;

import java.time.LocalDateTime;

public record RegisterResponse(
        String id,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt
) {}
