package com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entrada individual del libro IVA")
public record VatBookEntryResponse(
        UUID id,
        LocalDate issueDate,
        String voucherType,
        int pointOfSale,
        long voucherNumber,
        String taxpayerBusinessName,
        String taxpayerCuit,
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount
) {}
