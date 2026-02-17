package com.taxmanager.taxmanagerapi.auth.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.dto.LoginCommand;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.LoginUseCase;
import com.taxmanager.taxmanagerapi.auth.domain.entity.RefreshToken;
import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import com.taxmanager.taxmanagerapi.auth.domain.repository.RefreshTokenRepository;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.shared.exception.UnauthorizedException;
import com.taxmanager.taxmanagerapi.shared.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public LoginService(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtProvider jwtProvider,
                        @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public AuthTokenResult execute(LoginCommand command) {
        String email = command.email().trim().toLowerCase();

        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());

        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenValue, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return new AuthTokenResult(
                accessToken,
                refreshTokenValue,
                "Bearer",
                accessTokenExpiration / 1000  // seconds
        );
    }
}
