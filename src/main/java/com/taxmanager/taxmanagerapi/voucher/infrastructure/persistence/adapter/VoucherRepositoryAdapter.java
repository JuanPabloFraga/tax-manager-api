package com.taxmanager.taxmanagerapi.voucher.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.persistence.SpringDataVoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class VoucherRepositoryAdapter implements VoucherRepository {

    private final SpringDataVoucherRepository jpaRepository;

    public VoucherRepositoryAdapter(SpringDataVoucherRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Voucher save(Voucher voucher) {
        return jpaRepository.save(voucher);
    }

    @Override
    public Optional<Voucher> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Voucher> findAllByTaxpayerId(UUID taxpayerId, Pageable pageable) {
        return jpaRepository.findAllByTaxpayerId(taxpayerId, pageable);
    }

    @Override
    public Page<Voucher> findAllByTaxpayerIdAndCategory(UUID taxpayerId,
                                                         VoucherCategory category,
                                                         Pageable pageable) {
        return jpaRepository.findAllByTaxpayerIdAndCategory(taxpayerId, category, pageable);
    }

    @Override
    public List<Voucher> findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
            VoucherCategory category, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
                category, startDate, endDate);
    }
}
