package com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Libro IVA completo para un período")
public record VatBookResponse(
        String period,
        String category,
        List<VatBookEntryResponse> vouchers,
        VatBookTotalsResponse totals,
        int voucherCount
) {}
