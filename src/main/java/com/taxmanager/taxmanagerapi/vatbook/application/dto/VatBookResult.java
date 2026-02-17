package com.taxmanager.taxmanagerapi.vatbook.application.dto;

import java.time.YearMonth;
import java.util.List;

import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;

public record VatBookResult(
        String period,
        VoucherCategory category,
        List<VatBookEntryResult> vouchers,
        VatBookTotalsResult totals,
        int voucherCount
) {}
