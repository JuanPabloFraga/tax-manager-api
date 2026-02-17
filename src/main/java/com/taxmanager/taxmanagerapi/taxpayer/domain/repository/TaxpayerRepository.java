package com.taxmanager.taxmanagerapi.taxpayer.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaxpayerRepository {

    Taxpayer save(Taxpayer taxpayer);

    Optional<Taxpayer> findById(UUID id);

    Optional<Taxpayer> findByCuitAndActiveTrue(String cuit);

    Page<Taxpayer> findAllByActiveTrue(Pageable pageable);

    boolean existsByCuitAndActiveTrue(String cuit);
}