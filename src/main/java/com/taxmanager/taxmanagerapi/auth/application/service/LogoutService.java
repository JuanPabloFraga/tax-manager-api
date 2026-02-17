package com.taxmanager.taxmanagerapi.auth.application.service;

import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.LogoutUseCase;
import com.taxmanager.taxmanagerapi.auth.domain.entity.RefreshToken;
import com.taxmanager.taxmanagerapi.auth.domain.repository.RefreshTokenRepository;
import com.taxmanager.taxmanagerapi.shared.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void execute(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }
}
