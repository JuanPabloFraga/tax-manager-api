package com.taxmanager.taxmanagerapi.taxpayer.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;

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
@Table(name = "taxpayers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA only
public class Taxpayer {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(nullable = false, unique = true, length = 11)
    private String cuit;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_condition", nullable = false, length = 30)
    private TaxCondition taxCondition;

    @Column(name = "fiscal_address", nullable = false, length = 300)
    private String fiscalAddress;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ── Factory method ───────────────────────────────────────────────────

    public static Taxpayer create(String businessName,
                                  String cuit,
                                  TaxCondition taxCondition,
                                  String fiscalAddress,
                                  String email,
                                  String phone) {
        var taxpayer = new Taxpayer();
        taxpayer.id = UUID.randomUUID();
        taxpayer.active = true;
        taxpayer.setBusinessName(businessName);
        taxpayer.setCuit(cuit);
        taxpayer.setTaxCondition(taxCondition);
        taxpayer.setFiscalAddress(fiscalAddress);
        taxpayer.email = email;
        taxpayer.phone = phone;
        return taxpayer;
    }

    // ── Behavior methods ─────────────────────────────────────────────────

    public void updateInfo(String businessName,
                           TaxCondition taxCondition,
                           String fiscalAddress,
                           String email,
                           String phone) {
        setBusinessName(businessName);
        setTaxCondition(taxCondition);
        setFiscalAddress(fiscalAddress);
        this.email = email;
        this.phone = phone;
    }

    public void deactivate() {
        if (!this.active) {
            throw new DomainValidationException("El contribuyente ya está desactivado");
        }
        this.active = false;
    }

    // ── Private validation setters ───────────────────────────────────────

    private void setBusinessName(String businessName) {
        if (businessName == null || businessName.isBlank()) {
            throw new DomainValidationException("La razón social no puede estar vacía");
        }
        this.businessName = businessName.trim();
    }

    private void setCuit(String cuit) {
        if (cuit == null || cuit.length() != 11) {
            throw new DomainValidationException("El CUIT debe tener exactamente 11 dígitos");
        }
        if (!CuitValidator.isValid(cuit)) {
            throw new DomainValidationException("El CUIT no es válido (dígito verificador incorrecto)");
        }
        this.cuit = cuit;
    }

    private void setTaxCondition(TaxCondition taxCondition) {
        if (taxCondition == null) {
            throw new DomainValidationException("La condición fiscal es obligatoria");
        }
        this.taxCondition = taxCondition;
    }

    private void setFiscalAddress(String fiscalAddress) {
        if (fiscalAddress == null || fiscalAddress.isBlank()) {
            throw new DomainValidationException("El domicilio fiscal no puede estar vacío");
        }
        this.fiscalAddress = fiscalAddress.trim();
    }
}