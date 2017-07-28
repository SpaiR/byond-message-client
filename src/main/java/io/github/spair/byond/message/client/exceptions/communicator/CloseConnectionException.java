package io.github.spair.byond.message.client.exceptions.communicator;

/**
 * General exception for closing connection process.
 */
public class CloseConnectionException extends RuntimeException {

    public CloseConnectionException(Throwable cause) {
        super(cause);
    }
}
