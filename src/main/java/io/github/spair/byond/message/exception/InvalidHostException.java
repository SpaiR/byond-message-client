package io.github.spair.byond.message.exception;

import io.github.spair.byond.message.ServerAddress;

/**
 * Thrown if host address name is invalid. For example if IP is 999.999.999.999.
 */
public class InvalidHostException extends RuntimeException {

    private ServerAddress hostAddress;

    public InvalidHostException(final String message, final ServerAddress hostAddress) {
        super(message);
        this.hostAddress = hostAddress;
    }

    @SuppressWarnings("unused")
    public ServerAddress getHostAddress() {
        return hostAddress;
    }
}
