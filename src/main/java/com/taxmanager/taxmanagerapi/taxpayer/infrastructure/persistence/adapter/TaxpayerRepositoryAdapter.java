package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.persistence.SpringDataTaxpayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class TaxpayerRepositoryAdapter implements TaxpayerRepository {

    private final SpringDataTaxpayerRepository jpaRepository;

    public TaxpayerRepositoryAdapter(SpringDataTaxpayerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Taxpayer save(Taxpayer taxpayer) {
        return jpaRepository.save(taxpayer);
    }

    @Override
    public Optional<Taxpayer> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Taxpayer> findByCuitAndActiveTrue(String cuit) {
        return jpaRepository.findByCuitAndActiveTrue(cuit);
    }

    @Override
    public Page<Taxpayer> findAllByActiveTrue(Pageable pageable) {
        return jpaRepository.findAllByActiveTrue(pageable);
    }

    @Override
    public boolean existsByCuitAndActiveTrue(String cuit) {
        return jpaRepository.existsByCuitAndActiveTrue(cuit);
    }
}