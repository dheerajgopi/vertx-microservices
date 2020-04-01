package org.example.microservicecommon.exception;

/**
 * Wrapper for exceptions happening in service class level.
 */
public abstract class ServiceException extends RuntimeException {

    /**
     * HTTP status corresponding to the exception.
     */
    protected Integer httpStatus;

    public ServiceException(final String s) {
        super(s);
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }
}
