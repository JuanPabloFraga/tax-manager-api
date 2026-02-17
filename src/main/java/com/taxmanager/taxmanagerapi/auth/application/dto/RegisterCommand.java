package com.taxmanager.taxmanagerapi.auth.application.dto;

public record RegisterCommand(
        String email,
        String password,
        String fullName
) {}
