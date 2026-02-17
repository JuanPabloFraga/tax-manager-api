package com.taxmanager.taxmanagerapi.shared.fiscal;

/**
 * Validates Argentine CUIT/CUIL numbers using the <em>módulo 11</em> algorithm.
 *
 * <p>A valid CUIT has exactly 11 digits. The last digit is a check digit computed
 * by weighting each of the first 10 digits against the sequence {@code 5,4,3,2,7,6,5,4,3,2},
 * summing the products, and deriving the remainder from division by 11.</p>
 *
 * <p>This class works with the <strong>raw format</strong> (11 digits, no hyphens).
 * Use {@link #format(String)} to convert to the display format {@code XX-XXXXXXXX-X}
 * and {@link #strip(String)} to convert from display format to raw.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * CuitValidator.isValid("20123456783");          // true or false
 * CuitValidator.format("20123456783");            // "20-12345678-3"
 * CuitValidator.strip("20-12345678-3");           // "20123456783"
 * }</pre>
 */
public final class CuitValidator {

    private static final int CUIT_LENGTH = 11;
    private static final int[] WEIGHTS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    private CuitValidator() {
        // Utility class — no instantiation
    }

    /**
     * Validates a raw CUIT (11 digits, no hyphens).
     *
     * @param cuit the raw CUIT string
     * @return {@code true} if the CUIT is structurally valid and the check digit matches
     */
    public static boolean isValid(String cuit) {
        if (cuit == null || cuit.length() != CUIT_LENGTH) {
            return false;
        }

        // All characters must be digits
        for (int i = 0; i < CUIT_LENGTH; i++) {
            if (!Character.isDigit(cuit.charAt(i))) {
                return false;
            }
        }

        int sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            sum += Character.getNumericValue(cuit.charAt(i)) * WEIGHTS[i];
        }

        int remainder = sum % 11;
        int expectedCheckDigit = 11 - remainder;

        // Special cases per AFIP algorithm
        if (expectedCheckDigit == 11) {
            expectedCheckDigit = 0;
        } else if (expectedCheckDigit == 10) {
            // CUIT with check digit 10 is invalid (some sources map to 9, but
            // the strict interpretation is that such CUITs don't exist)
            return false;
        }

        int actualCheckDigit = Character.getNumericValue(cuit.charAt(10));
        return expectedCheckDigit == actualCheckDigit;
    }

    /**
     * Formats a raw CUIT into display format: {@code XX-XXXXXXXX-X}.
     *
     * @param rawCuit the 11-digit raw CUIT
     * @return formatted CUIT with hyphens
     * @throws IllegalArgumentException if the input is not exactly 11 digits
     */
    public static String format(String rawCuit) {
        if (rawCuit == null || rawCuit.length() != CUIT_LENGTH) {
            throw new IllegalArgumentException("Raw CUIT must be exactly 11 digits, got: " + rawCuit);
        }
        return "%s-%s-%s".formatted(
                rawCuit.substring(0, 2),
                rawCuit.substring(2, 10),
                rawCuit.substring(10)
        );
    }

    /**
     * Strips hyphens from a formatted CUIT ({@code XX-XXXXXXXX-X → XXXXXXXXXXX}).
     *
     * @param formattedCuit the CUIT with hyphens
     * @return raw 11-digit CUIT
     */
    public static String strip(String formattedCuit) {
        if (formattedCuit == null) {
            return null;
        }
        return formattedCuit.replace("-", "");
    }
}
