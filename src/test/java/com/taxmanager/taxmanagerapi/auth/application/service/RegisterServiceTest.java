package com.taxmanager.taxmanagerapi.auth.application.service;

import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterCommand;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterResult;
import com.taxmanager.taxmanagerapi.auth.domain.enums.UserRole;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.shared.exception.ConflictException;
import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterService registerService;

    @Test
    @DisplayName("Registra usuario correctamente con email normalizado y rol ACCOUNTANT")
    void registerSuccess() {
        when(userRepository.existsByEmailAndActiveTrue("juan@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RegisterCommand command = new RegisterCommand("Juan@Email.COM", "Password123!", "Juan Pérez");
        RegisterResult result = registerService.execute(command);

        assertNotNull(result.id());
        assertEquals("juan@email.com", result.email());
        assertEquals("Juan Pérez", result.fullName());
        assertEquals(UserRole.ACCOUNTANT.name(), result.role());
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("Falla si ya existe un usuario con ese email")
    void failsWhenEmailAlreadyExists() {
        when(userRepository.existsByEmailAndActiveTrue("juan@email.com")).thenReturn(true);

        RegisterCommand command = new RegisterCommand("juan@email.com", "Password123!", "Juan Pérez");
        assertThrows(ConflictException.class, () -> registerService.execute(command));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Falla si la contraseña tiene menos de 8 caracteres")
    void failsWhenPasswordTooShort() {
        RegisterCommand command = new RegisterCommand("juan@email.com", "Short1!", "Juan Pérez");
        assertThrows(DomainValidationException.class, () -> registerService.execute(command));

        verify(userRepository, never()).save(any());
    }
}
