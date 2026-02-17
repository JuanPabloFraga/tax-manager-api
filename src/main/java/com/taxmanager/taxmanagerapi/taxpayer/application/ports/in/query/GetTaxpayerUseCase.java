package com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.query;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerItemResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetTaxpayerUseCase {
    Page<TaxpayerItemResult> findAll(Pageable pageable);
    TaxpayerDetailResult findById(UUID id);
    TaxpayerDetailResult findByCuit(String cuit);
}