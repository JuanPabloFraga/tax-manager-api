package com.taxmanager.taxmanagerapi.vatbook.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;

public record VatBookEntryResult(
        UUID id,
        LocalDate issueDate,
        VoucherType voucherType,
        int pointOfSale,
        long voucherNumber,
        String taxpayerBusinessName,
        String taxpayerCuit,
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount
) {}
