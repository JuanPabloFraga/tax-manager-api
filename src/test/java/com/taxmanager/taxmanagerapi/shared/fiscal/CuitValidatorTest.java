package com.taxmanager.taxmanagerapi.shared.fiscal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuitValidatorTest {

    @Nested
    @DisplayName("isValid()")
    class IsValid {

        @ParameterizedTest
        @ValueSource(strings = {
                "20123456786",  // Persona física masculina (check digit 6)
                "27234567891",  // Persona física femenina (check digit 1)
                "30712345671",  // Persona jurídica (check digit 1)
                "20000000028",  // Edge case: many zeros (check digit 8)
                "33693450239"   // Real-world CUIT (check digit 9)
        })
        @DisplayName("should return true for valid CUITs")
        void validCuits(String cuit) {
            assertTrue(CuitValidator.isValid(cuit));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "20123456780",  // Wrong check digit (expected 6)
                "20123456789",  // Wrong check digit (expected 6)
                "11111111111",  // Wrong check digit (expected 3)
                "99999999999"   // Wrong check digit
        })
        @DisplayName("should return false for CUITs with wrong check digit")
        void invalidCheckDigit(String cuit) {
            assertFalse(CuitValidator.isValid(cuit));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should return false for null or empty input")
        void nullOrEmpty(String cuit) {
            assertFalse(CuitValidator.isValid(cuit));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2012345678",    // 10 digits
                "201234567830",  // 12 digits
                "20-12345678-3", // With hyphens
                "2012345678a",   // Contains letter
                "abcdefghijk"    // All letters
        })
        @DisplayName("should return false for malformed input")
        void malformedInput(String cuit) {
            assertFalse(CuitValidator.isValid(cuit));
        }
    }

    @Nested
    @DisplayName("format()")
    class Format {

        @Test
        @DisplayName("should format raw CUIT with hyphens")
        void formatsCorrectly() {
            assertEquals("20-12345678-6", CuitValidator.format("20123456786"));
            assertEquals("30-71234567-1", CuitValidator.format("30712345671"));
        }

        @Test
        @DisplayName("should throw for invalid length")
        void throwsForInvalidLength() {
            assertThrows(IllegalArgumentException.class, () -> CuitValidator.format("123"));
            assertThrows(IllegalArgumentException.class, () -> CuitValidator.format(null));
        }
    }

    @Nested
    @DisplayName("strip()")
    class Strip {

        @Test
        @DisplayName("should remove hyphens from formatted CUIT")
        void stripsHyphens() {
            assertEquals("20123456786", CuitValidator.strip("20-12345678-6"));
        }

        @Test
        @DisplayName("should return same string if no hyphens")
        void noHyphens() {
            assertEquals("20123456786", CuitValidator.strip("20123456786"));
        }

        @Test
        @DisplayName("should return null for null input")
        void nullInput() {
            assertNull(CuitValidator.strip(null));
        }
    }
}
