package io.github.spair.byond.message.client.exceptions.communicator;

/**
 * General exception for opening connection with BYOND server.
 */
public class OpenConnectionException extends RuntimeException {

    public OpenConnectionException(Throwable cause) {
        super(cause);
    }
}
