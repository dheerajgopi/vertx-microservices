package org.example.microservicecommon.exception;

/**
 * Exception thrown when the sort field provided in the URL in incorrect.
 */
public class InvalidSortFieldException extends ServiceException {

    public InvalidSortFieldException(final String message) {
        super(message);
        this.httpStatus = 400;
    }

}
