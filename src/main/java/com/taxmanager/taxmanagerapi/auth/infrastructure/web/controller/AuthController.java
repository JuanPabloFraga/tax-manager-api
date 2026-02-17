package com.taxmanager.taxmanagerapi.auth.infrastructure.web.controller;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterResult;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.LoginUseCase;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.LogoutUseCase;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.RefreshTokenUseCase;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.RegisterUseCase;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.AuthTokenResponse;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.LoginRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.LogoutRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.MessageResponse;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.RefreshTokenRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.RegisterRequest;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.dto.RegisterResponse;
import com.taxmanager.taxmanagerapi.auth.infrastructure.web.mapper.AuthWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Autenticación y autorización")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthWebMapper mapper;

    public AuthController(RegisterUseCase registerUseCase,
                          LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase,
                          AuthWebMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResult result = registerUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthTokenResult result = loginUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @Operation(summary = "Renovar access token con refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthTokenResult result = refreshTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @Operation(summary = "Cerrar sesión (revocar refresh token)")
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody LogoutRequest request) {
        logoutUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente"));
    }
}
