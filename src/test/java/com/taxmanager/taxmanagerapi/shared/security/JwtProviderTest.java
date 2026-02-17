package com.taxmanager.taxmanagerapi.shared.security;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // Secret must be ≥ 256 bits for HS256
        String secret = "test-secret-key-min-256-bits-para-tests-del-proyecto-tax-manager-api!!";
        jwtProvider = new JwtProvider(secret, 900000L); // 15 min
    }

    @Test
    @DisplayName("Genera un token válido y extrae claims correctamente")
    void generateAndParseClaims() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateAccessToken(userId, "admin@test.com", "ADMIN");

        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUserIdFromToken(token));
        assertEquals("admin@test.com", jwtProvider.getEmailFromToken(token));
        assertEquals("ADMIN", jwtProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Token inválido no pasa validación")
    void invalidToken() {
        assertFalse(jwtProvider.validateToken("token.invalido.abc"));
    }

    @Test
    @DisplayName("Token null no pasa validación")
    void nullToken() {
        assertFalse(jwtProvider.validateToken(null));
    }

    @Test
    @DisplayName("Token firmado con otra clave no pasa validación")
    void tokenWithDifferentKey() {
        JwtProvider otherProvider = new JwtProvider(
                "other-secret-key-min-256-bits-para-tests-del-proyecto-tax-manager-api!!", 900000L);
        String token = otherProvider.generateAccessToken(UUID.randomUUID(), "x@x.com", "ADMIN");

        assertFalse(jwtProvider.validateToken(token));
    }

    @Test
    @DisplayName("Token expirado no pasa validación")
    void expiredToken() {
        JwtProvider shortLived = new JwtProvider(
                "test-secret-key-min-256-bits-para-tests-del-proyecto-tax-manager-api!!", 0L);
        String token = shortLived.generateAccessToken(UUID.randomUUID(), "x@x.com", "ADMIN");

        // token generated with 0ms expiration — already expired
        assertFalse(jwtProvider.validateToken(token));
    }
}
