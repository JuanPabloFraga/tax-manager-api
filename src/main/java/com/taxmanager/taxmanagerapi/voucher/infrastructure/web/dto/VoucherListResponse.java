package com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de comprobante para listado")
public record VoucherListResponse(
        UUID id,
        String category,
        String voucherType,
        LocalDate issueDate,
        int pointOfSale,
        long voucherNumber,
        BigDecimal totalAmount,
        String description
) {}
