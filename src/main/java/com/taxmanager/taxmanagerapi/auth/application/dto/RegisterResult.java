package com.taxmanager.taxmanagerapi.auth.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterResult(
        UUID id,
        String email,
        String fullName,
        String role,
        LocalDateTime createdAt
) {}
