package com.taxmanager.taxmanagerapi.auth.infrastructure.web.mapper;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.dto.LoginCommand;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterCommand;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterResult;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.AuthTokenResponse;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.LoginRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.RegisterRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthWebMapper {

    public RegisterCommand toCommand(RegisterRequest request) {
        return new RegisterCommand(
                request.email(),
                request.password(),
                request.fullName());
    }

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(
                request.email(),
                request.password());
    }

    public RegisterResponse toResponse(RegisterResult result) {
        return new RegisterResponse(
                result.id().toString(),
                result.email(),
                result.fullName(),
                result.role(),
                result.createdAt());
    }

    public AuthTokenResponse toResponse(AuthTokenResult result) {
        return new AuthTokenResponse(
                result.accessToken(),
                result.refreshToken(),
                result.tokenType(),
                result.expiresIn());
    }
}
