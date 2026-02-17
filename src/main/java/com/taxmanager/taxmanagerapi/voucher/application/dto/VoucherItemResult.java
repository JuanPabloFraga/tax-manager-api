package com.taxmanager.taxmanagerapi.voucher.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;

public record VoucherItemResult(
        UUID id,
        VoucherCategory category,
        VoucherType voucherType,
        LocalDate issueDate,
        int pointOfSale,
        long voucherNumber,
        BigDecimal totalAmount,
        String description
) {}
