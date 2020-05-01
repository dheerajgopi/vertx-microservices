package org.example.microservicecommon.exception;

/**
 * Exception thrown when the resource to be fetched is not found.
 * Eg: while fetching from DB.
 */
public class ResourceNotFoundException extends ServiceException {

    public ResourceNotFoundException(final String message) {
        super(message);
        this.httpStatus = 404;
    }

}
