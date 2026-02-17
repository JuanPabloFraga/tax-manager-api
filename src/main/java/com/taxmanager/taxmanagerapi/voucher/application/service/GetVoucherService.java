package com.taxmanager.taxmanagerapi.voucher.application.service;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherItemResult;
import com.taxmanager.taxmanagerapi.voucher.application.ports.in.query.GetVoucherUseCase;
import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetVoucherService implements GetVoucherUseCase {

    private final VoucherRepository voucherRepository;
    private final TaxpayerRepository taxpayerRepository;

    public GetVoucherService(VoucherRepository voucherRepository,
                             TaxpayerRepository taxpayerRepository) {
        this.voucherRepository = voucherRepository;
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public VoucherDetailResult findById(UUID id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró comprobante con ID " + id));

        Taxpayer taxpayer = taxpayerRepository.findById(voucher.getTaxpayerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró contribuyente con ID " + voucher.getTaxpayerId()));

        return toDetailResult(voucher, taxpayer);
    }

    @Override
    public Page<VoucherItemResult> findAllByTaxpayerId(UUID taxpayerId,
                                                        VoucherCategory category,
                                                        Pageable pageable) {
        // Verificar que el contribuyente existe
        if (taxpayerRepository.findById(taxpayerId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontró contribuyente con ID " + taxpayerId);
        }

        Page<Voucher> page;
        if (category != null) {
            page = voucherRepository.findAllByTaxpayerIdAndCategory(taxpayerId, category, pageable);
        } else {
            page = voucherRepository.findAllByTaxpayerId(taxpayerId, pageable);
        }

        return page.map(this::toItemResult);
    }

    private VoucherDetailResult toDetailResult(Voucher v, Taxpayer t) {
        return new VoucherDetailResult(
                v.getId(), v.getTaxpayerId(),
                t.getBusinessName(), CuitValidator.format(t.getCuit()),
                v.getCategory(), v.getVoucherType(),
                v.getIssueDate(), v.getPointOfSale(), v.getVoucherNumber(),
                v.getNetAmount(), v.getVatAmount(), v.getExemptAmount(), v.getTotalAmount(),
                v.getDescription(), v.getCreatedAt()
        );
    }

    private VoucherItemResult toItemResult(Voucher v) {
        return new VoucherItemResult(
                v.getId(), v.getCategory(), v.getVoucherType(),
                v.getIssueDate(), v.getPointOfSale(), v.getVoucherNumber(),
                v.getTotalAmount(), v.getDescription()
        );
    }
}
