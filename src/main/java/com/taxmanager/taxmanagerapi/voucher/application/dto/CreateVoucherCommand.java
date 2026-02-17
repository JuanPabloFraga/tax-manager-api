package com.taxmanager.taxmanagerapi.voucher.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;

public record CreateVoucherCommand(
        VoucherCategory category,
        VoucherType voucherType,
        LocalDate issueDate,
        int pointOfSale,
        long voucherNumber,
        BigDecimal netAmount,
        BigDecimal vatAmount,
        BigDecimal exemptAmount,
        BigDecimal totalAmount,
        String description
) {}
