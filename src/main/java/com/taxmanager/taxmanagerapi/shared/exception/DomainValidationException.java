package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Thrown when a domain invariant is violated.
 * Maps to HTTP 422 Unprocessable Entity.
 *
 * <p>Examples: invalid CUIT checksum, net + vat + exempt ≠ total,
 * business name blank when creating a Taxpayer.</p>
 */
public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(message);
    }
}
