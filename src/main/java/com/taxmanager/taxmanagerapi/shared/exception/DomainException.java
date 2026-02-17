package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Abstract base for all domain exceptions.
 * Each subclass maps to a specific HTTP status code
 * and is handled by {@link GlobalExceptionHandler}.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
