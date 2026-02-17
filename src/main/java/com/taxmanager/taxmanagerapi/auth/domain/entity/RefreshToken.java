package com.taxmanager.taxmanagerapi.auth.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class RefreshToken {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Factory method ───────────────────────────────────────────────────

    public static RefreshToken create(UUID userId, String token, LocalDateTime expiresAt) {
        var rt = new RefreshToken();
        rt.id = UUID.randomUUID();
        rt.revoked = false;
        rt.setUserId(userId);
        rt.setToken(token);
        rt.setExpiresAt(expiresAt);
        return rt;
    }

    // ── Behavior ─────────────────────────────────────────────────────────

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isUsable() {
        return !this.revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }

    // ── Private validation setters ───────────────────────────────────────

    private void setUserId(UUID userId) {
        if (userId == null) {
            throw new DomainValidationException("El ID del usuario es obligatorio");
        }
        this.userId = userId;
    }

    private void setToken(String token) {
        if (token == null || token.isBlank()) {
            throw new DomainValidationException("El token es obligatorio");
        }
        this.token = token;
    }

    private void setExpiresAt(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            throw new DomainValidationException("La fecha de expiración es obligatoria");
        }
        this.expiresAt = expiresAt;
    }
}
