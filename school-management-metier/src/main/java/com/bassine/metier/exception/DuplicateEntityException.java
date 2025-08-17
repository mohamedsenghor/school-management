package com.bassine.metier.exception;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateEntityException(String entityName, String field, String value) {
        super(String.format("%s avec %s '%s' existe déjà", entityName, field, value));
    }
}