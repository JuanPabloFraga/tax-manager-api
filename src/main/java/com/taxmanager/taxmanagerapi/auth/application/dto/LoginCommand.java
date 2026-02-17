package com.taxmanager.taxmanagerapi.auth.application.dto;

public record LoginCommand(
        String email,
        String password
) {}
