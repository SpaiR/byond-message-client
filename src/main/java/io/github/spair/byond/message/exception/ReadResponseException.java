package io.github.spair.byond.message.exception;

/**
 * General exception for reading response from BYOND server.
 */
public class ReadResponseException extends RuntimeException {

    public ReadResponseException(Throwable cause) {
        super(cause);
    }
}
