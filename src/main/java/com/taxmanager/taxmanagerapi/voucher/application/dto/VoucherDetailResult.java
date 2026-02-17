package com.taxmanager.taxmanagerapi.voucher.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;

public record VoucherDetailResult(
        UUID id,
        UUID taxpayerId,
        String taxpayerBusinessName,
        String taxpayerCuit,
        VoucherCategory category,
        VoucherType voucherType,
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
