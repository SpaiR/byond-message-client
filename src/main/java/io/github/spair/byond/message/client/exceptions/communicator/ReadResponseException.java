package io.github.spair.byond.message.client.exceptions.communicator;

/**
 * General exception for reading response from BYOND server.
 */
public class ReadResponseException extends RuntimeException {

    public ReadResponseException(Throwable cause) {
        super(cause);
    }
}
