package io.github.spair.byond.message.exception;

/**
 * Signals that actual BYOND response somehow has unexpected behavior.
 * For example, if response type isn't equal to expected or when response is empty, while something expected.
 */
public class UnexpectedResponseException extends RuntimeException {

    public UnexpectedResponseException(String message) {
        super(message);
    }
}
