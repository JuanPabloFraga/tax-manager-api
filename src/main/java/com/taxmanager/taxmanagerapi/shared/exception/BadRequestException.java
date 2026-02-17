package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Thrown when the request payload is structurally invalid
 * beyond what Bean Validation catches.
 * Maps to HTTP 400 Bad Request.
 */
public class BadRequestException extends DomainException {

    public BadRequestException(String message) {
        super(message);
    }
}
