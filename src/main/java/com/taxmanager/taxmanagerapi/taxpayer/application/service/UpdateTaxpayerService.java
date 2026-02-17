package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.UpdateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.UpdateTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateTaxpayerService implements UpdateTaxpayerUseCase {

    private final TaxpayerRepository taxpayerRepository;

    public UpdateTaxpayerService(TaxpayerRepository taxpayerRepository) {
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public TaxpayerDetailResult execute(UUID id, UpdateTaxpayerCommand command) {
        Taxpayer taxpayer = taxpayerRepository.findById(id)
                .filter(Taxpayer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Taxpayer", id));

        taxpayer.updateInfo(
                command.businessName(),
                command.taxCondition(),
                command.fiscalAddress(),
                command.email(),
                command.phone()
        );

        taxpayer = taxpayerRepository.save(taxpayer);
        return toDetailResult(taxpayer);
    }

    private TaxpayerDetailResult toDetailResult(Taxpayer t) {
        return new TaxpayerDetailResult(
                t.getId(), t.getBusinessName(), t.getCuit(), t.getTaxCondition(),
                t.getFiscalAddress(), t.getEmail(), t.getPhone(),
                t.isActive(), t.getCreatedAt(), t.getUpdatedAt()
        );
    }
}