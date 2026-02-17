package com.taxmanager.taxmanagerapi.shared.exception;

/**
 * Thrown when a requested resource does not exist or is inactive.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("%s not found with identifier: %s".formatted(resourceName, identifier));
    }
}
