package io.github.spair.byond.message.client.exceptions.converter;

/**
 * General exception for conversion of message before it send.
 */
public class ConvertMessageException extends RuntimeException {

    public ConvertMessageException(Throwable cause) {
        super(cause);
    }
}
