package com.taxmanager.taxmanagerapi.auth.domain.entity;

import com.taxmanager.taxmanagerapi.auth.domain.enums.UserRole;
import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Crear usuario válido genera ID, rol ACCOUNTANT y active=true")
    void createValid() {
        User user = User.create("Juan@Email.COM", "encodedPassword123", "Juan Pérez");

        assertNotNull(user.getId());
        assertEquals("juan@email.com", user.getEmail()); // normalized to lowercase
        assertEquals("encodedPassword123", user.getPassword());
        assertEquals("Juan Pérez", user.getFullName());
        assertEquals(UserRole.ACCOUNTANT, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Falla si email es null")
    void failsWhenEmailNull() {
        assertThrows(DomainValidationException.class, () ->
                User.create(null, "encodedPassword123", "Juan Pérez"));
    }

    @Test
    @DisplayName("Falla si email es blank")
    void failsWhenEmailBlank() {
        assertThrows(DomainValidationException.class, () ->
                User.create("  ", "encodedPassword123", "Juan Pérez"));
    }

    @Test
    @DisplayName("Falla si email no contiene @")
    void failsWhenEmailInvalid() {
        assertThrows(DomainValidationException.class, () ->
                User.create("juanemail.com", "encodedPassword123", "Juan Pérez"));
    }

    @Test
    @DisplayName("Falla si password es null")
    void failsWhenPasswordNull() {
        assertThrows(DomainValidationException.class, () ->
                User.create("juan@email.com", null, "Juan Pérez"));
    }

    @Test
    @DisplayName("Falla si password es blank")
    void failsWhenPasswordBlank() {
        assertThrows(DomainValidationException.class, () ->
                User.create("juan@email.com", "  ", "Juan Pérez"));
    }

    @Test
    @DisplayName("Falla si fullName es null")
    void failsWhenFullNameNull() {
        assertThrows(DomainValidationException.class, () ->
                User.create("juan@email.com", "encodedPassword123", null));
    }

    @Test
    @DisplayName("Falla si fullName es blank")
    void failsWhenFullNameBlank() {
        assertThrows(DomainValidationException.class, () ->
                User.create("juan@email.com", "encodedPassword123", "   "));
    }

    @Test
    @DisplayName("deactivate() desactiva un usuario activo")
    void deactivateActiveUser() {
        User user = User.create("juan@email.com", "encodedPassword123", "Juan Pérez");
        user.deactivate();
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("deactivate() falla si el usuario ya está desactivado")
    void deactivateAlreadyInactive() {
        User user = User.create("juan@email.com", "encodedPassword123", "Juan Pérez");
        user.deactivate();
        assertThrows(DomainValidationException.class, user::deactivate);
    }

    @Test
    @DisplayName("El email se trimea y normaliza a lowercase")
    void emailNormalization() {
        User user = User.create("  ADMIN@Test.COM  ", "encodedPassword123", "Admin");
        assertEquals("admin@test.com", user.getEmail());
    }

    @Test
    @DisplayName("El fullName se trimea")
    void fullNameTrimmed() {
        User user = User.create("juan@email.com", "encodedPassword123", "  Juan Pérez  ");
        assertEquals("Juan Pérez", user.getFullName());
    }
}
