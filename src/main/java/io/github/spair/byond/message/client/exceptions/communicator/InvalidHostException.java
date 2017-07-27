package io.github.spair.byond.message.client.exceptions.communicator;

import io.github.spair.byond.message.ServerAddress;
import lombok.Getter;

public class InvalidHostException extends RuntimeException {

    @Getter
    private ServerAddress hostAddress;

    public InvalidHostException(String message, ServerAddress hostAddress) {
        super(message);
        this.hostAddress = hostAddress;
    }
}
