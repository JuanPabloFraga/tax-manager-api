package com.taxmanager.taxmanagerapi.taxpayer.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;

public record TaxpayerDetailResult(
        UUID id,
        String businessName,
        String cuit,
        TaxCondition taxCondition,
        String fiscalAddress,
        String email,
        String phone,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}