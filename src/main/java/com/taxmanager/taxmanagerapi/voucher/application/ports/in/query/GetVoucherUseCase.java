package com.taxmanager.taxmanagerapi.voucher.application.ports.in.query;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherItemResult;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetVoucherUseCase {

    VoucherDetailResult findById(UUID id);

    Page<VoucherItemResult> findAllByTaxpayerId(UUID taxpayerId, VoucherCategory category, Pageable pageable);
}
