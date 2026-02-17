package com.taxmanager.taxmanagerapi.auth.application.service;

import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterCommand;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterResult;
import com.taxmanager.taxmanagerapi.auth.application.ports.in.command.RegisterUseCase;
import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.shared.exception.ConflictException;
import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterService implements RegisterUseCase {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResult execute(RegisterCommand command) {
        validatePassword(command.password());

        String email = command.email().trim().toLowerCase();

        if (userRepository.existsByEmailAndActiveTrue(email)) {
            throw new ConflictException("Ya existe un usuario registrado con el email " + email);
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.create(email, encodedPassword, command.fullName());
        user = userRepository.save(user);

        return new RegisterResult(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new DomainValidationException(
                    "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
    }
}
