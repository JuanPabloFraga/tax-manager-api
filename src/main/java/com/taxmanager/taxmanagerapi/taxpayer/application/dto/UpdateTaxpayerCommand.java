package com.taxmanager.taxmanagerapi.taxpayer.application.dto;

import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;

public record UpdateTaxpayerCommand(
        String businessName,
        TaxCondition taxCondition,
        String fiscalAddress,
        String email,
        String phone
) {}