package org.example.microservicecommon.exception;

import java.util.Arrays;
import java.util.List;

/**
 * Exception thrown when a key is missing or its value is empty in the request body.
 */
public class MissingOrEmptyKeyException extends ServiceException {

    /**
     * Missing keys.
     */
    private List<String> keys;

    public MissingOrEmptyKeyException(final String key, final String message) {
        super(message);
        this.httpStatus = 400;
        this.keys = Arrays.asList(key);
    }

    public MissingOrEmptyKeyException(final List<String> keys, final String message) {
        super(message);
        this.httpStatus = 400;
        this.keys = keys;
    }

    public List<String> getKeys() {
        return keys;
    }
}
