package com.taxmanager.taxmanagerapi.auth.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.auth.domain.entity.User;
import com.taxmanager.taxmanagerapi.auth.domain.repository.UserRepository;
import com.taxmanager.taxmanagerapi.auth.infrastructure.persistence.SpringDataUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmailAndActiveTrue(String email) {
        return jpaRepository.findByEmailAndActiveTrue(email);
    }

    @Override
    public boolean existsByEmailAndActiveTrue(String email) {
        return jpaRepository.existsByEmailAndActiveTrue(email);
    }
}
