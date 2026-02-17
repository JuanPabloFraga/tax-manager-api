package com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.mapper;

import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookEntryResult;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookResult;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookTotalsResult;
import com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto.VatBookEntryResponse;
import com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto.VatBookResponse;
import com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto.VatBookTotalsResponse;
import org.springframework.stereotype.Component;

@Component
public class VatBookWebMapper {

    public VatBookResponse toResponse(VatBookResult result) {
        return new VatBookResponse(
                result.period(),
                result.category().name(),
                result.vouchers().stream().map(this::toEntryResponse).toList(),
                toTotalsResponse(result.totals()),
                result.voucherCount()
        );
    }

    private VatBookEntryResponse toEntryResponse(VatBookEntryResult entry) {
        return new VatBookEntryResponse(
                entry.id(),
                entry.issueDate(),
                entry.voucherType().name(),
                entry.pointOfSale(),
                entry.voucherNumber(),
                entry.taxpayerBusinessName(),
                entry.taxpayerCuit(),
                entry.netAmount(),
                entry.vatAmount(),
                entry.exemptAmount(),
                entry.totalAmount()
        );
    }

    private VatBookTotalsResponse toTotalsResponse(VatBookTotalsResult totals) {
        return new VatBookTotalsResponse(
                totals.netAmount(),
                totals.vatAmount(),
                totals.exemptAmount(),
                totals.totalAmount()
        );
    }
}
