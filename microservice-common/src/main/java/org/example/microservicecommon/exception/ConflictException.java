package org.example.microservicecommon.exception;

/**
 * Exception thrown when the a resource has conflicting values.
 */
public class ConflictException extends ServiceException {

    /**
     * Field having conflicting value.
     */
    final String field;

    public ConflictException(final String field, final String message) {
        super(message);
        this.field = field;
        this.httpStatus = 409;
    }

    public String getField() {
        return field;
    }
}
