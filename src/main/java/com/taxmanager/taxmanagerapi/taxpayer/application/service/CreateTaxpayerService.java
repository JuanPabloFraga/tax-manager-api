package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import com.taxmanager.taxmanagerapi.shared.exception.ConflictException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.CreateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.CreateTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateTaxpayerService implements CreateTaxpayerUseCase {

    private final TaxpayerRepository taxpayerRepository;

    public CreateTaxpayerService(TaxpayerRepository taxpayerRepository) {
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public TaxpayerDetailResult execute(CreateTaxpayerCommand command) {
        String rawCuit = CuitValidator.strip(command.cuit());

        if (taxpayerRepository.existsByCuitAndActiveTrue(rawCuit)) {
            throw new ConflictException("Ya existe un contribuyente activo con CUIT " + command.cuit());
        }

        Taxpayer taxpayer = Taxpayer.create(
                command.businessName(),
                rawCuit,
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