package org.example.microservicecommon.exception;

public class ResourceNotFoundException extends ServiceException {

    public ResourceNotFoundException(final String message) {
        super(message);
        this.httpStatus = 404;
    }

}
