package io.github.spair.byond.message.client.exceptions;

/**
 * Thrown when actual response type doesn't to equals to what expected.
 */
public class UnexpectedResponseTypeException extends RuntimeException {

    public UnexpectedResponseTypeException(String message) {
        super(message);
    }
}
