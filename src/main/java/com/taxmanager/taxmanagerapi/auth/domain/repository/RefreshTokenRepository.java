package com.taxmanager.taxmanagerapi.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.entity.RefreshToken;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeAllByUserId(UUID userId);
}
