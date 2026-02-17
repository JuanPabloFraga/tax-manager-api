package com.taxmanager.taxmanagerapi.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.entity.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByEmailAndActiveTrue(String email);
}
