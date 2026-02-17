package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Thrown when an operation conflicts with current state
 * (e.g. duplicate CUIT, duplicate email).
 * Maps to HTTP 409 Conflict.
 */
public class ConflictException extends DomainException {

    public ConflictException(String message) {
        super(message);
    }
}
