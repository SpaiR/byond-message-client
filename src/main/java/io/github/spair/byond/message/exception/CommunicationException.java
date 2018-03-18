package io.github.spair.byond.message.exception;

/**
 * General communication exception.
 */
public class CommunicationException extends RuntimeException {

    public CommunicationException(final Throwable cause) {
        super(cause);
    }
}
