package io.github.spair.byond.message.exception;

/**
 * General exception for sending message to BYOND server.
 */
public class SendMessageException extends RuntimeException {

    public SendMessageException(final Throwable cause) {
        super(cause);
    }
}
