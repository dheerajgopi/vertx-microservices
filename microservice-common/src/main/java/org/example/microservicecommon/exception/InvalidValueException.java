package org.example.microservicecommon.exception;

import java.util.Arrays;
import java.util.List;

/**
 * Exception thrown when the value of a key is invalid in the request body.
 */
public class InvalidValueException extends ServiceException {

    /**
     * Invalid keys.
     */
    private List<String> keys;

    public InvalidValueException(final String key, final String message) {
        super(message);
        this.httpStatus = 400;
        this.keys = Arrays.asList(key);
    }

    public InvalidValueException(final List<String> keys, final String message) {
        super(message);
        this.httpStatus = 400;
        this.keys = keys;
    }

    public List<String> getKeys() {
        return keys;
    }

}
