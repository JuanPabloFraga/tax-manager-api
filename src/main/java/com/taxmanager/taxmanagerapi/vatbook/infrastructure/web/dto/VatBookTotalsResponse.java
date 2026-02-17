package com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Totales del libro IVA")
public record VatBookTotalsResponse(
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount
) {}
