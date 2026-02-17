package com.taxmanager.taxmanagerapi.vatbook.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookResult;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetVatBookServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @InjectMocks
    private GetVatBookService service;

    @Test
    @DisplayName("Libro IVA Ventas con comprobantes calcula totales correctamente")
    void salesBookWithVouchers() {
        YearMonth period = YearMonth.of(2026, 2);

        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "30712345671", TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234, CABA", null, null
        );

        Voucher v1 = Voucher.create(
                taxpayer.getId(), VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 5), 1, 100,
                new BigDecimal("80000.0000"), new BigDecimal("16800.0000"),
                new BigDecimal("0.0000"), new BigDecimal("96800.0000"),
                null
        );

        Voucher v2 = Voucher.create(
                taxpayer.getId(), VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 101,
                new BigDecimal("55000.0000"), new BigDecimal("11550.0000"),
                new BigDecimal("0.0000"), new BigDecimal("66550.0000"),
                null
        );

        when(voucherRepository.findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
                eq(VoucherCategory.SALE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(v1, v2));

        when(taxpayerRepository.findById(taxpayer.getId()))
                .thenReturn(Optional.of(taxpayer));

        VatBookResult result = service.getSales(period);

        assertEquals("2026-02", result.period());
        assertEquals(VoucherCategory.SALE, result.category());
        assertEquals(2, result.voucherCount());
        assertEquals(2, result.vouchers().size());

        // Verificar totales
        assertEquals(0, new BigDecimal("135000.0000").compareTo(result.totals().netAmount()));
        assertEquals(0, new BigDecimal("28350.0000").compareTo(result.totals().vatAmount()));
        assertEquals(0, new BigDecimal("0.0000").compareTo(result.totals().exemptAmount()));
        assertEquals(0, new BigDecimal("163350.0000").compareTo(result.totals().totalAmount()));

        // Verificar datos del contribuyente en las entradas
        assertEquals("López S.R.L.", result.vouchers().getFirst().taxpayerBusinessName());
        assertEquals("30-71234567-1", result.vouchers().getFirst().taxpayerCuit());
    }

    @Test
    @DisplayName("Libro IVA Compras vacío devuelve totales en cero")
    void emptyPurchaseBook() {
        YearMonth period = YearMonth.of(2026, 3);

        when(voucherRepository.findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
                eq(VoucherCategory.PURCHASE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        VatBookResult result = service.getPurchases(period);

        assertEquals("2026-03", result.period());
        assertEquals(VoucherCategory.PURCHASE, result.category());
        assertEquals(0, result.voucherCount());
        assertTrue(result.vouchers().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.totals().netAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.totals().vatAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.totals().exemptAmount()));
        assertEquals(0, BigDecimal.ZERO.compareTo(result.totals().totalAmount()));
    }

    @Test
    @DisplayName("Usa rango de fechas correcto para el período")
    void usesCorrectDateRange() {
        YearMonth period = YearMonth.of(2026, 2);

        when(voucherRepository.findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
                eq(VoucherCategory.SALE),
                eq(LocalDate.of(2026, 2, 1)),
                eq(LocalDate.of(2026, 2, 28))))
                .thenReturn(Collections.emptyList());

        service.getSales(period);

        verify(voucherRepository).findAllByCategoryAndIssueDateBetweenOrderByIssueDateAsc(
                VoucherCategory.SALE,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 28)
        );
    }
}
