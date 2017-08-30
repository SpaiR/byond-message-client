package io.github.spair.byond.message.client.exceptions.communicator;

/**
 * General exception for sending message to BYOND server.
 */
public class SendMessageException extends RuntimeException {

    public SendMessageException(Throwable cause) {
        super(cause);
    }
}
