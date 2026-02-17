package com.taxmanager.taxmanagerapi.voucher.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherItemResult;
import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetVoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @InjectMocks
    private GetVoucherService service;

    @Test
    @DisplayName("Encuentra comprobante por ID con datos del contribuyente")
    void findByIdSuccessfully() {
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "30712345671", TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234, CABA", null, null
        );
        UUID taxpayerId = taxpayer.getId();

        Voucher voucher = Voucher.create(
                taxpayerId, VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 1542,
                new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                new BigDecimal("0.0000"), new BigDecimal("121000.0000"),
                "Consultoría"
        );

        when(voucherRepository.findById(voucher.getId())).thenReturn(Optional.of(voucher));
        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.of(taxpayer));

        VoucherDetailResult result = service.findById(voucher.getId());

        assertEquals(voucher.getId(), result.id());
        assertEquals("López S.R.L.", result.taxpayerBusinessName());
        assertEquals("30-71234567-1", result.taxpayerCuit());
        assertEquals(VoucherCategory.SALE, result.category());
    }

    @Test
    @DisplayName("Falla si comprobante no existe")
    void findByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(voucherRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    @DisplayName("Lista comprobantes de un contribuyente sin filtro de categoría")
    void findAllByTaxpayerIdWithoutCategory() {
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "30712345671", TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234, CABA", null, null
        );
        UUID taxpayerId = taxpayer.getId();

        Voucher voucher = Voucher.create(
                taxpayerId, VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 1542,
                new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                new BigDecimal("0.0000"), new BigDecimal("121000.0000"),
                null
        );

        Pageable pageable = PageRequest.of(0, 20);
        Page<Voucher> page = new PageImpl<>(List.of(voucher), pageable, 1);

        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.of(taxpayer));
        when(voucherRepository.findAllByTaxpayerId(taxpayerId, pageable)).thenReturn(page);

        Page<VoucherItemResult> result = service.findAllByTaxpayerId(taxpayerId, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(VoucherCategory.SALE, result.getContent().getFirst().category());
    }

    @Test
    @DisplayName("Lista comprobantes filtrados por categoría")
    void findAllByTaxpayerIdWithCategory() {
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "30712345671", TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234, CABA", null, null
        );
        UUID taxpayerId = taxpayer.getId();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Voucher> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.of(taxpayer));
        when(voucherRepository.findAllByTaxpayerIdAndCategory(taxpayerId, VoucherCategory.PURCHASE, pageable))
                .thenReturn(emptyPage);

        Page<VoucherItemResult> result = service.findAllByTaxpayerId(
                taxpayerId, VoucherCategory.PURCHASE, pageable);

        assertEquals(0, result.getTotalElements());
        verify(voucherRepository).findAllByTaxpayerIdAndCategory(taxpayerId, VoucherCategory.PURCHASE, pageable);
    }

    @Test
    @DisplayName("Falla al listar si el contribuyente no existe")
    void findAllByTaxpayerIdNotFound() {
        UUID taxpayerId = UUID.randomUUID();
        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                service.findAllByTaxpayerId(taxpayerId, null, PageRequest.of(0, 20)));
    }
}
