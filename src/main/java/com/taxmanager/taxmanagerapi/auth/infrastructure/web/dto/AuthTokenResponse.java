package com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}
