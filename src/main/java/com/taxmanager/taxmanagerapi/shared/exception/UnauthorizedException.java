package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Thrown when authentication fails (bad credentials, expired token, etc.).
 * Maps to HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
