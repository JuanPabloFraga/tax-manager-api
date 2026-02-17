package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.DeleteTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteTaxpayerService implements DeleteTaxpayerUseCase {

    private final TaxpayerRepository taxpayerRepository;

    public DeleteTaxpayerService(TaxpayerRepository taxpayerRepository) {
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public void execute(UUID id) {
        Taxpayer taxpayer = taxpayerRepository.findById(id)
                .filter(Taxpayer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Taxpayer", id));

        taxpayer.deactivate();
        taxpayerRepository.save(taxpayer);
    }
}