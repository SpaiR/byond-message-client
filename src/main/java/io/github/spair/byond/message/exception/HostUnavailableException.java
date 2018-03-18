package io.github.spair.byond.message.exception;

/**
 * Occurs while establishment connection to BYOND server and tells mainly that host is currently offline.
 */
public class HostUnavailableException extends RuntimeException {

    public HostUnavailableException(final String message) {
        super(message);
    }
}
