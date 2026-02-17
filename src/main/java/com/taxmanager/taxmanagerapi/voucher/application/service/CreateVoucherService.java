package com.taxmanager.taxmanagerapi.voucher.application.service;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.voucher.application.dto.CreateVoucherCommand;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.application.ports.in.command.CreateVoucherUseCase;
import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateVoucherService implements CreateVoucherUseCase {

    private final VoucherRepository voucherRepository;
    private final TaxpayerRepository taxpayerRepository;

    public CreateVoucherService(VoucherRepository voucherRepository,
                                TaxpayerRepository taxpayerRepository) {
        this.voucherRepository = voucherRepository;
        this.taxpayerRepository = taxpayerRepository;
    }

    @Override
    public VoucherDetailResult execute(UUID taxpayerId, CreateVoucherCommand command) {
        Taxpayer taxpayer = taxpayerRepository.findById(taxpayerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró contribuyente con ID " + taxpayerId));

        Voucher voucher = Voucher.create(
                taxpayerId,
                command.category(),
                command.voucherType(),
                command.issueDate(),
                command.pointOfSale(),
                command.voucherNumber(),
                command.netAmount(),
                command.vatAmount(),
                command.exemptAmount(),
                command.totalAmount(),
                command.description()
        );

        voucher = voucherRepository.save(voucher);
        return toDetailResult(voucher, taxpayer);
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
}
