package io.github.spair.byond.message.client.exceptions;

/**
 * Thrown by {@link io.github.spair.byond.message.client.ByondClient}
 * when response type in {@link io.github.spair.byond.message.ByondMessage}
 * doesn't equals to actual type in {@link io.github.spair.byond.message.response.ByondResponse}.
 */
public class UnexpectedResponseTypeException extends RuntimeException {

    public UnexpectedResponseTypeException(String message) {
        super(message);
    }
}
