package com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle completo de un comprobante")
public record VoucherDetailResponse(
        UUID id,
        UUID taxpayerId,
        String taxpayerBusinessName,
        String taxpayerCuit,
        String category,
        String voucherType,
        LocalDate issueDate,
        int pointOfSale,
        long voucherNumber,
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount,
        String description,
        LocalDateTime createdAt
) {}
