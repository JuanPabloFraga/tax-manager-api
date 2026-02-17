package com.taxmanager.taxmanagerapi.voucher.infrastructure.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataVoucherRepository extends JpaRepository<Voucher, UUID> {

    Page<Voucher> findAllByTaxpayerId(UUID taxpayerId, Pageable pageable);

    Page<Voucher> findAllByTaxpayerIdAndCategory(UUID taxpayerId, VoucherCategory category, Pageable pageable);

    List<Voucher> findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
            VoucherCategory category, LocalDate startDate, LocalDate endDate);
}
