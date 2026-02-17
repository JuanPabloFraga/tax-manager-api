package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle completo de un contribuyente")
public record TaxpayerDetailResponse(
        UUID id,
        String businessName,
        @Schema(description = "CUIT con guiones", example = "30-71234567-1")
        String cuit,
        String taxCondition,
        String fiscalAddress,
        String email,
        String phone,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}