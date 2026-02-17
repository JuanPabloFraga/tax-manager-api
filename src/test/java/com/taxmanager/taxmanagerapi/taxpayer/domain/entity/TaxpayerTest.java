package com.taxmanager.taxmanagerapi.taxpayer.domain.entity;

import com.taxmanager.taxmanagerapi.shared.exception.DomainValidationException;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TaxpayerTest {

    // Valid test data (CUITs verified with módulo 11)
    private static final String VALID_CUIT = "20123456786";
    private static final String VALID_NAME = "López S.R.L.";
    private static final TaxCondition VALID_CONDITION = TaxCondition.RESPONSABLE_INSCRIPTO;
    private static final String VALID_ADDRESS = "Av. Corrientes 1234, CABA";

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create a valid taxpayer with all fields")
        void createWithAllFields() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, "test@test.com", "+54 11 1234-5678"
            );

            assertNotNull(taxpayer.getId());
            assertEquals(VALID_NAME, taxpayer.getBusinessName());
            assertEquals(VALID_CUIT, taxpayer.getCuit());
            assertEquals(VALID_CONDITION, taxpayer.getTaxCondition());
            assertEquals(VALID_ADDRESS, taxpayer.getFiscalAddress());
            assertEquals("test@test.com", taxpayer.getEmail());
            assertEquals("+54 11 1234-5678", taxpayer.getPhone());
            assertTrue(taxpayer.isActive());
        }

        @Test
        @DisplayName("should create taxpayer with optional fields null")
        void createWithOptionalFieldsNull() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );

            assertNotNull(taxpayer.getId());
            assertEquals(VALID_NAME, taxpayer.getBusinessName());
            assertNull(taxpayer.getEmail());
            assertNull(taxpayer.getPhone());
        }

        @Test
        @DisplayName("should trim business name")
        void trimBusinessName() {
            Taxpayer taxpayer = Taxpayer.create(
                    "  López S.R.L.  ", VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );
            assertEquals("López S.R.L.", taxpayer.getBusinessName());
        }

        @Test
        @DisplayName("should throw when business name is blank")
        void blankBusinessName() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create("", VALID_CUIT, VALID_CONDITION,
                            VALID_ADDRESS, null, null)
            );
        }

        @Test
        @DisplayName("should throw when business name is null")
        void nullBusinessName() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create(null, VALID_CUIT, VALID_CONDITION,
                            VALID_ADDRESS, null, null)
            );
        }

        @Test
        @DisplayName("should throw when CUIT has wrong length")
        void wrongCuitLength() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create(VALID_NAME, "123", VALID_CONDITION,
                            VALID_ADDRESS, null, null)
            );
        }

        @Test
        @DisplayName("should throw when CUIT has invalid check digit")
        void invalidCuitCheckDigit() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create(VALID_NAME, "20123456780", VALID_CONDITION,
                            VALID_ADDRESS, null, null)
            );
        }

        @Test
        @DisplayName("should throw when tax condition is null")
        void nullTaxCondition() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create(VALID_NAME, VALID_CUIT, null,
                            VALID_ADDRESS, null, null)
            );
        }

        @Test
        @DisplayName("should throw when fiscal address is blank")
        void blankFiscalAddress() {
            assertThrows(DomainValidationException.class, () ->
                    Taxpayer.create(VALID_NAME, VALID_CUIT, VALID_CONDITION,
                            "  ", null, null)
            );
        }
    }

    @Nested
    @DisplayName("updateInfo()")
    class UpdateInfo {

        @Test
        @DisplayName("should update all mutable fields")
        void updateAllFields() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );

            taxpayer.updateInfo("Nuevo Nombre S.A.", TaxCondition.MONOTRIBUTISTA,
                    "Nueva Dirección 999", "nuevo@test.com", "+54 11 9999-0000");

            assertEquals("Nuevo Nombre S.A.", taxpayer.getBusinessName());
            assertEquals(TaxCondition.MONOTRIBUTISTA, taxpayer.getTaxCondition());
            assertEquals("Nueva Dirección 999", taxpayer.getFiscalAddress());
            assertEquals("nuevo@test.com", taxpayer.getEmail());
        }

        @Test
        @DisplayName("should throw when updating with blank name")
        void updateWithBlankName() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );

            assertThrows(DomainValidationException.class, () ->
                    taxpayer.updateInfo("", VALID_CONDITION, VALID_ADDRESS, null, null)
            );
        }
    }

    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("should set active to false")
        void deactivate() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );

            taxpayer.deactivate();
            assertFalse(taxpayer.isActive());
        }

        @Test
        @DisplayName("should throw when already inactive")
        void deactivateAlreadyInactive() {
            Taxpayer taxpayer = Taxpayer.create(
                    VALID_NAME, VALID_CUIT, VALID_CONDITION,
                    VALID_ADDRESS, null, null
            );
            taxpayer.deactivate();

            assertThrows(DomainValidationException.class, taxpayer::deactivate);
        }
    }
}