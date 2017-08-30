package io.github.spair.byond.message.client.exceptions.converter;

/**
 * General exception for conversion of BYOND server response into {@link io.github.spair.byond.message.response.ByondResponse}.
 */
public class ConvertResponseException extends RuntimeException {

    public ConvertResponseException(Throwable cause) {
        super(cause);
    }
}
