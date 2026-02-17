package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTaxpayerRepository extends JpaRepository<Taxpayer, UUID> {

    Optional<Taxpayer> findByCuitAndActiveTrue(String cuit);

    Page<Taxpayer> findAllByActiveTrue(Pageable pageable);

    boolean existsByCuitAndActiveTrue(String cuit);
}