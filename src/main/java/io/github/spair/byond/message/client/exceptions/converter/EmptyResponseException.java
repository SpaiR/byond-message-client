package io.github.spair.byond.message.client.exceptions.converter;

/**
 * Signals that response from BYOND is empty, but user expected something to get.
 */
public class EmptyResponseException extends RuntimeException {

    public EmptyResponseException(String message) {
        super(message);
    }
}
