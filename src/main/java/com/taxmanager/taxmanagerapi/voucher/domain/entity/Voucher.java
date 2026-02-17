package com.taxmanager.taxmanagerapi.voucher.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "vouchers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class Voucher {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "taxpayer_id", nullable = false, updatable = false)
    private UUID taxpayerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VoucherCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false, length = 20)
    private VoucherType voucherType;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "point_of_sale", nullable = false)
    private int pointOfSale;

    @Column(name = "voucher_number", nullable = false)
    private long voucherNumber;

    @Column(name = "net_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal netAmount;

    @Column(name = "vat_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal vatAmount;

    @Column(name = "exempt_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal exemptAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Factory method ───────────────────────────────────────────────────

    public static Voucher create(UUID taxpayerId,
                                 VoucherCategory category,
                                 VoucherType voucherType,
                                 LocalDate issueDate,
                                 int pointOfSale,
                                 long voucherNumber,
                                 BigDecimal netAmount,
                                 BigDecimal vatAmount,
                                 BigDecimal exemptAmount,
                                 BigDecimal totalAmount,
                                 String description) {
        var voucher = new Voucher();
        voucher.id = UUID.randomUUID();
        voucher.setTaxpayerId(taxpayerId);
        voucher.setCategory(category);
        voucher.setVoucherType(voucherType);
        voucher.setIssueDate(issueDate);
        voucher.setPointOfSale(pointOfSale);
        voucher.setVoucherNumber(voucherNumber);
        voucher.setAmounts(netAmount, vatAmount, exemptAmount, totalAmount);
        voucher.description = description;
        return voucher;
    }

    // ── Private validation setters ───────────────────────────────────────

    private void setTaxpayerId(UUID taxpayerId) {
        if (taxpayerId == null) {
            throw new DomainValidationException("El ID del contribuyente es obligatorio");
        }
        this.taxpayerId = taxpayerId;
    }

    private void setCategory(VoucherCategory category) {
        if (category == null) {
            throw new DomainValidationException("La categoría del comprobante es obligatoria");
        }
        this.category = category;
    }

    private void setVoucherType(VoucherType voucherType) {
        if (voucherType == null) {
            throw new DomainValidationException("El tipo de comprobante es obligatorio");
        }
        this.voucherType = voucherType;
    }

    private void setIssueDate(LocalDate issueDate) {
        if (issueDate == null) {
            throw new DomainValidationException("La fecha de emisión es obligatoria");
        }
        this.issueDate = issueDate;
    }

    private void setPointOfSale(int pointOfSale) {
        if (pointOfSale < 1 || pointOfSale > 99999) {
            throw new DomainValidationException("El punto de venta debe estar entre 1 y 99999");
        }
        this.pointOfSale = pointOfSale;
    }

    private void setVoucherNumber(long voucherNumber) {
        if (voucherNumber <= 0) {
            throw new DomainValidationException("El número de comprobante debe ser mayor a 0");
        }
        this.voucherNumber = voucherNumber;
    }

    private void setAmounts(BigDecimal netAmount,
                            BigDecimal vatAmount,
                            BigDecimal exemptAmount,
                            BigDecimal totalAmount) {
        if (netAmount == null || vatAmount == null || exemptAmount == null || totalAmount == null) {
            throw new DomainValidationException("Todos los montos son obligatorios");
        }
        if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("El monto neto no puede ser negativo");
        }
        if (vatAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("El monto de IVA no puede ser negativo");
        }
        if (exemptAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("El monto exento no puede ser negativo");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("El monto total debe ser mayor a 0");
        }

        BigDecimal expectedTotal = netAmount.add(vatAmount).add(exemptAmount);
        if (expectedTotal.compareTo(totalAmount) != 0) {
            throw new DomainValidationException(
                    "Los montos no cuadran: neto + IVA + exento (" + expectedTotal +
                    ") ≠ total (" + totalAmount + ")");
        }

        this.netAmount = netAmount;
        this.vatAmount = vatAmount;
        this.exemptAmount = exemptAmount;
        this.totalAmount = totalAmount;
    }
}
