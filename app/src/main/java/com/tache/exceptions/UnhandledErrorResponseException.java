package com.tache.exceptions;

/**
 * Created by ujjwal on 8/13/16.
 */

public class UnhandledErrorResponseException extends Exception {
    public UnhandledErrorResponseException() {
        super();
    }

    public UnhandledErrorResponseException(String message) {
        super(message);
    }

    public UnhandledErrorResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnhandledErrorResponseException(Throwable cause) {
        super(cause);
    }
}
