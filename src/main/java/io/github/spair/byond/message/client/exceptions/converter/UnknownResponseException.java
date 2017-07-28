package io.github.spair.byond.message.client.exceptions.converter;

/**
 * Signals that response bytes encoded in unknown format.
 */
public class UnknownResponseException extends RuntimeException {

    public UnknownResponseException(String message) {
        super(message);
    }
}
