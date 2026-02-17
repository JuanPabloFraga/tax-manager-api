package com.taxmanager.taxmanagerapi.auth.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.RefreshTokenUseCase;
import com.taxmanager.taxmanagerapi.auth.domain.entity.RefreshToken;
import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import com.taxmanager.taxmanagerapi.auth.domain.repository.RefreshTokenRepository;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.shared.exception.UnauthorizedException;
import com.taxmanager.taxmanagerapi.shared.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtProvider jwtProvider,
                               @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                               @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public AuthTokenResult execute(String refreshTokenValue) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (!existingToken.isUsable()) {
            throw new UnauthorizedException("Refresh token expirado o revocado");
        }

        // Refresh token rotation: revoke old, create new
        existingToken.revoke();
        refreshTokenRepository.save(existingToken);

        User user = userRepository.findById(existingToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String newAccessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());

        String newRefreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        RefreshToken newRefreshToken = RefreshToken.create(
                user.getId(), newRefreshTokenValue, expiresAt);
        refreshTokenRepository.save(newRefreshToken);

        return new AuthTokenResult(
                newAccessToken,
                newRefreshTokenValue,
                "Bearer",
                accessTokenExpiration / 1000
        );
    }
}
