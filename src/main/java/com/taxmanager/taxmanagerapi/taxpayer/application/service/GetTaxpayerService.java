package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerItemResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.query.GetTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetTaxpayerService implements GetTaxpayerUseCase {

    private final TaxpayerRepository taxpayerRepository;

    public GetTaxpayerService(TaxpayerRepository taxpayerRepository) {
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public Page<TaxpayerItemResult> findAll(Pageable pageable) {
        return taxpayerRepository.findAllByActiveTrue(pageable)
                .map(this::toItemResult);
    }

    @Override
    public TaxpayerDetailResult findById(UUID id) {
        Taxpayer taxpayer = taxpayerRepository.findById(id)
                .filter(Taxpayer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Taxpayer", id));
        return toDetailResult(taxpayer);
    }

    @Override
    public TaxpayerDetailResult findByCuit(String cuit) {
        String rawCuit = CuitValidator.strip(cuit);
        Taxpayer taxpayer = taxpayerRepository.findByCuitAndActiveTrue(rawCuit)
                .orElseThrow(() -> new ResourceNotFoundException("Taxpayer", cuit));
        return toDetailResult(taxpayer);
    }

    private TaxpayerItemResult toItemResult(Taxpayer t) {
        return new TaxpayerItemResult(
                t.getId(), t.getBusinessName(), t.getCuit(),
                t.getTaxCondition(), t.isActive()
        );
    }

    private TaxpayerDetailResult toDetailResult(Taxpayer t) {
        return new TaxpayerDetailResult(
                t.getId(), t.getBusinessName(), t.getCuit(), t.getTaxCondition(),
                t.getFiscalAddress(), t.getEmail(), t.getPhone(),
                t.isActive(), t.getCreatedAt(), t.getUpdatedAt()
        );
    }
}