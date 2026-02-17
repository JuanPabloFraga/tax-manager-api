package com.taxmanager.taxmanagerapi.auth.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.entity.RefreshToken;
import com.taxmanager.taxmanagerapi.auth.domain.repository.RefreshTokenRepository;
import com.taxmanager.taxmanagerapi.auth.infrastructure.persistence.SpringDataRefreshTokenRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        jpaRepository.revokeAllByUserId(userId);
    }
}
