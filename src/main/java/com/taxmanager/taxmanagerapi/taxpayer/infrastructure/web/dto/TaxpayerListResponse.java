package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item de contribuyente para listados")
public record TaxpayerListResponse(
        UUID id,
        String businessName,
        @Schema(description = "CUIT con guiones", example = "30-71234567-1")
        String cuit,
        String taxCondition,
        boolean active
) {}