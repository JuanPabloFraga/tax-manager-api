package com.taxmanager.taxmanagerapi.auth.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.enums.UserRole;
import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Factory method ───────────────────────────────────────────────────

    public static User create(String email, String encodedPassword, String fullName) {
        var user = new User();
        user.id = UUID.randomUUID();
        user.active = true;
        user.role = UserRole.ACCOUNTANT; // Default role
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setFullName(fullName);
        return user;
    }

    // ── Behavior ─────────────────────────────────────────────────────────

    public void deactivate() {
        if (!this.active) {
            throw new DomainValidationException("El usuario ya está desactivado");
        }
        this.active = false;
    }

    // ── Private validation setters ───────────────────────────────────────

    private void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("El email es obligatorio");
        }
        if (!email.contains("@")) {
            throw new DomainValidationException("El email no tiene un formato válido");
        }
        this.email = email.trim().toLowerCase();
    }

    private void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new DomainValidationException("La contraseña es obligatoria");
        }
        this.password = password;
    }

    private void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainValidationException("El nombre completo es obligatorio");
        }
        this.fullName = fullName.trim();
    }
}
