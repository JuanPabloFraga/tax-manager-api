package com.taxmanager.taxmanagerapi.auth.application.service;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.dto.LoginCommand;
import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import com.taxmanager.taxmanagerapi.auth.domain.repository.RefreshTokenRepository;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.shared.exception.UnauthorizedException;
import com.taxmanager.taxmanagerapi.shared.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("Login exitoso devuelve tokens y tipo Bearer")
    void loginSuccess() {
        // given
        User user = User.create("juan@email.com", "$2a$encoded", "Juan Pérez");
        when(userRepository.findByEmailAndActiveTrue("juan@email.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "$2a$encoded")).thenReturn(true);
        when(jwtProvider.generateAccessToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("jwt-access-token");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoginService loginService = new LoginService(
                userRepository, refreshTokenRepository, passwordEncoder,
                jwtProvider, 900000L, 604800000L);

        // when
        LoginCommand command = new LoginCommand("juan@email.com", "Password123!");
        AuthTokenResult result = loginService.execute(command);

        // then
        assertEquals("jwt-access-token", result.accessToken());
        assertNotNull(result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(900L, result.expiresIn());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Falla si no existe el usuario")
    void failsWhenUserNotFound() {
        when(userRepository.findByEmailAndActiveTrue("noexiste@email.com"))
                .thenReturn(Optional.empty());

        LoginService loginService = new LoginService(
                userRepository, refreshTokenRepository, passwordEncoder,
                jwtProvider, 900000L, 604800000L);

        LoginCommand command = new LoginCommand("noexiste@email.com", "Password123!");
        assertThrows(UnauthorizedException.class, () -> loginService.execute(command));
    }

    @Test
    @DisplayName("Falla si la contraseña es incorrecta")
    void failsWhenPasswordWrong() {
        User user = User.create("juan@email.com", "$2a$encoded", "Juan Pérez");
        when(userRepository.findByEmailAndActiveTrue("juan@email.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass!", "$2a$encoded")).thenReturn(false);

        LoginService loginService = new LoginService(
                userRepository, refreshTokenRepository, passwordEncoder,
                jwtProvider, 900000L, 604800000L);

        LoginCommand command = new LoginCommand("juan@email.com", "WrongPass!");
        assertThrows(UnauthorizedException.class, () -> loginService.execute(command));
    }
}
