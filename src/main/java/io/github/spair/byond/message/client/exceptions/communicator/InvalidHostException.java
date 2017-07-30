package io.github.spair.byond.message.client.exceptions.communicator;

import io.github.spair.byond.message.ServerAddress;
import lombok.Getter;

/**
 * Thrown if host address name is invalid. For example if IP is 999.999.999.999.
 */
public class InvalidHostException extends RuntimeException {

    @Getter
    private ServerAddress hostAddress;

    public InvalidHostException(String message, ServerAddress hostAddress) {
        super(message);
        this.hostAddress = hostAddress;
    }
}