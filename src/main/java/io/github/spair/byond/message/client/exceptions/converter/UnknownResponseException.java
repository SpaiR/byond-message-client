package io.github.spair.byond.message.client.exceptions.converter;

/**
 * Signals that BYOND response encoded in unknown format.
 */
public class UnknownResponseException extends RuntimeException {

    public UnknownResponseException(String message) {
        super(message);
    }
}
