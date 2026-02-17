package com.taxmanager.taxmanagerapi.taxpayer.application.dto;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;

public record TaxpayerItemResult(
        UUID id,
        String businessName,
        String cuit,
        TaxCondition taxCondition,
        boolean active
) {}