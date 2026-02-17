package com.taxmanager.taxmanagerapi.auth.application.dto;

public record AuthTokenResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}
