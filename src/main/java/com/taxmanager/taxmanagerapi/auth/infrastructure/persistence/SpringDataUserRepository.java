package com.taxmanager.taxmanagerapi.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndActiveTrue(String email);

    boolean existsByEmailAndActiveTrue(String email);
}
