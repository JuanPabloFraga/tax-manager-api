package com.taxmanager.taxmanagerapi.voucher.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherRepository {

    Voucher save(Voucher voucher);

    Optional<Voucher> findById(UUID id);

    Page<Voucher> findAllByTaxpayerId(UUID taxpayerId, Pageable pageable);

    Page<Voucher> findAllByTaxpayerIdAndCategory(UUID taxpayerId, VoucherCategory category, Pageable pageable);

    List<Voucher> findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
            VoucherCategory category, LocalDate startDate, LocalDate endDate);
}
