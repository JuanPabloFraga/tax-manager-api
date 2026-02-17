package com.taxmanager.taxmanagerapi.voucher.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoucherTest {

    private static final UUID TAXPAYER_ID = UUID.randomUUID();

    @Test
    @DisplayName("Crear voucher válido genera ID y asigna todos los campos")
    void createValid() {
        Voucher voucher = Voucher.create(
                TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 1542,
                new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                new BigDecimal("0.0000"), new BigDecimal("121000.0000"),
                "Consultoría"
        );

        assertNotNull(voucher.getId());
        assertEquals(TAXPAYER_ID, voucher.getTaxpayerId());
        assertEquals(VoucherCategory.SALE, voucher.getCategory());
        assertEquals(VoucherType.FACTURA_A, voucher.getVoucherType());
        assertEquals(LocalDate.of(2026, 2, 15), voucher.getIssueDate());
        assertEquals(1, voucher.getPointOfSale());
        assertEquals(1542, voucher.getVoucherNumber());
        assertEquals(0, new BigDecimal("100000.0000").compareTo(voucher.getNetAmount()));
        assertEquals(0, new BigDecimal("21000.0000").compareTo(voucher.getVatAmount()));
        assertEquals(0, new BigDecimal("0.0000").compareTo(voucher.getExemptAmount()));
        assertEquals(0, new BigDecimal("121000.0000").compareTo(voucher.getTotalAmount()));
        assertEquals("Consultoría", voucher.getDescription());
    }

    @Test
    @DisplayName("Falla si montos no cuadran: net + vat + exempt ≠ total")
    void failsWhenAmountsDontMatch() {
        DomainValidationException ex = assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 1, 1542,
                        new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("999999.0000"),
                        null
                )
        );
        assertTrue(ex.getMessage().contains("no cuadran"));
    }

    @Test
    @DisplayName("Falla si monto neto es negativo")
    void failsWhenNetAmountNegative() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.PURCHASE, VoucherType.FACTURA_B,
                        LocalDate.of(2026, 1, 10), 3, 100,
                        new BigDecimal("-1.0000"), new BigDecimal("0.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("1.0000"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si total es cero")
    void failsWhenTotalIsZero() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 1, 1542,
                        BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO,
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si taxpayerId es null")
    void failsWhenTaxpayerIdNull() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        null, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 1, 1542,
                        new BigDecimal("100.0000"), new BigDecimal("21.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("121.0000"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si punto de venta es 0")
    void failsWhenPointOfSaleZero() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 0, 1542,
                        new BigDecimal("100.0000"), new BigDecimal("21.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("121.0000"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si número de comprobante es negativo")
    void failsWhenVoucherNumberNegative() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 1, -1,
                        new BigDecimal("100.0000"), new BigDecimal("21.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("121.0000"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si categoría es null")
    void failsWhenCategoryNull() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, null, VoucherType.FACTURA_A,
                        LocalDate.of(2026, 2, 15), 1, 1542,
                        new BigDecimal("100.0000"), new BigDecimal("21.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("121.0000"),
                        null
                )
        );
    }

    @Test
    @DisplayName("Falla si fecha de emisión es null")
    void failsWhenIssueDateNull() {
        assertThrows(DomainValidationException.class, () ->
                Voucher.create(
                        TAXPAYER_ID, VoucherCategory.SALE, VoucherType.FACTURA_A,
                        null, 1, 1542,
                        new BigDecimal("100.0000"), new BigDecimal("21.0000"),
                        new BigDecimal("0.0000"), new BigDecimal("121.0000"),
                        null
                )
        );
    }
}
