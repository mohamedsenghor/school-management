package com.bassine.metier.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s avec l'ID %d non trouvé", entityName, id));
    }
}