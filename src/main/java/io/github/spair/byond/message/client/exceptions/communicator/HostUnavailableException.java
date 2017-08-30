package io.github.spair.byond.message.client.exceptions.communicator;

/**
 * Occurs while establishment connection to BYOND server and tells mainly that host is currently offline.
 */
public class HostUnavailableException extends RuntimeException {

    public HostUnavailableException(String message) {
        super(message);
    }
}
