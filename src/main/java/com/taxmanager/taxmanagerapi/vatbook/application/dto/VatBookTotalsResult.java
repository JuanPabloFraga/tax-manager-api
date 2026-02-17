package com.taxmanager.taxmanagerapi.vatbook.application.dto;

import java.math.BigDecimal;

public record VatBookTotalsResult(
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount
) {}
