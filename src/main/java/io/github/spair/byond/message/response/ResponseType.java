package io.github.spair.byond.message.response;

/**
 * Represents what type of response expected from BYOND,
 * when used in {@link io.github.spair.byond.message.ByondMessage}
 * and actual returned data type in {@link io.github.spair.byond.message.response.ByondResponse}.
 */
public enum ResponseType {

    /**
     * In case of using in {@link io.github.spair.byond.message.ByondMessage} means,
     * that no response is expected and method will return empty {@link io.github.spair.byond.message.response.ByondResponse} instance.
     * In {@link io.github.spair.byond.message.response.ByondResponse} it means, that 'responseData' field is 'null'.
     */
    NONE,

    /**
     * Any type of data is expected. Also default value for {@link io.github.spair.byond.message.ByondMessage}.
     */
    ANY,

    /**
     * Representation of {@link java.lang.Float} type.
     */
    FLOAT_NUMBER,

    /**
     * Representation of {@link java.lang.String} type.
     */
    STRING
}
