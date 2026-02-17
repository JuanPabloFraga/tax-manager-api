package com.taxmanager.taxmanagerapi.taxpayer.application.dto;

import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;

public record CreateTaxpayerCommand(
        String businessName,
        String cuit,
        TaxCondition taxCondition,
        String fiscalAddress,
        String email,
        String phone
) {}