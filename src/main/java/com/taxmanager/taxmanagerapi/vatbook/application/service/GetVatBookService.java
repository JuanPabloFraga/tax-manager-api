package com.taxmanager.taxmanagerapi.vatbook.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookEntryResult;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookResult;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookTotalsResult;
import com.taxmanager.taxmanagerapi.vatbook.application.ports.in.query.GetVatBookUseCase;
import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetVatBookService implements GetVatBookUseCase {

    private final VoucherRepository voucherRepository;
    private final TaxpayerRepository taxpayerRepository;

    public GetVatBookService(VoucherRepository voucherRepository,
                             TaxpayerRepository taxpayerRepository) {
        this.voucherRepository = voucherRepository;
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public VatBookResult getPurchases(YearMonth period) {
        return buildVatBook(period, VoucherCategory.PURCHASE);
    }

    @Override
    public VatBookResult getSales(YearMonth period) {
        return buildVatBook(period, VoucherCategory.SALE);
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private VatBookResult buildVatBook(YearMonth period, VoucherCategory category) {
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();

        List<Voucher> vouchers = voucherRepository
                .findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(category, startDate, endDate);

        // Cargar contribuyentes en batch para evitar N+1
        List<UUID> taxpayerIds = vouchers.stream()
                .map(Voucher::getTaxpayerId)
                .distinct()
                .toList();

        Map<UUID, Taxpayer> taxpayerMap = taxpayerIds.stream()
                .map(id -> taxpayerRepository.findById(id).orElse(null))
                .filter(t -> t != null)
                .collect(Collectors.toMap(Taxpayer::getId, Function.identity()));

        List<VatBookEntryResult> entries = vouchers.stream()
                .map(v -> toEntry(v, taxpayerMap.get(v.getTaxpayerId())))
                .toList();

        VatBookTotalsResult totals = calculateTotals(vouchers);

        return new VatBookResult(
                period.toString(),
                category,
                entries,
                totals,
                entries.size()
        );
    }

    private VatBookEntryResult toEntry(Voucher v, Taxpayer t) {
        String businessName = t != null ? t.getBusinessName() : "Contribuyente eliminado";
        String cuit = t != null ? CuitValidator.format(t.getCuit()) : "00-00000000-0";

        return new VatBookEntryResult(
                v.getId(),
                v.getIssueDate(),
                v.getVoucherType(),
                v.getPointOfSale(),
                v.getVoucherNumber(),
                businessName,
                cuit,
                v.getNetAmount(),
                v.getVatAmount(),
                v.getExemptAmount(),
                v.getTotalAmount()
        );
    }

    private VatBookTotalsResult calculateTotals(List<Voucher> vouchers) {
        BigDecimal netTotal = BigDecimal.ZERO;
        BigDecimal vatTotal = BigDecimal.ZERO;
        BigDecimal exemptTotal = BigDecimal.ZERO;
        BigDecimal totalTotal = BigDecimal.ZERO;

        for (Voucher v : vouchers) {
            netTotal = netTotal.add(v.getNetAmount());
            vatTotal = vatTotal.add(v.getVatAmount());
            exemptTotal = exemptTotal.add(v.getExemptAmount());
            totalTotal = totalTotal.add(v.getTotalAmount());
        }

        return new VatBookTotalsResult(netTotal, vatTotal, exemptTotal, totalTotal);
    }
}
