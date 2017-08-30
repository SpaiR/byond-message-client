package io.github.spair.byond.message.response;

/**
 * Represents what type of response expected from BYOND,
 * when used in {@link io.github.spair.byond.message.ByondMessage}
 * and actual returned data type in {@link io.github.spair.byond.message.response.ByondResponse}.
 */
public enum ResponseType {

    /**
     * No response is expected. That means, that client will only send message, without waiting for response.
     * <br>
     * <b>Used only in {@link io.github.spair.byond.message.ByondMessage}</b>.
     */
    NONE,

    /**
     * Any type of data is expected.
     * <br>
     * <b>Default value and used only in {@link io.github.spair.byond.message.ByondMessage}</b>.
     */
    ANY,

    /**
     * Represent of {@link java.lang.Float} type.
     */
    FLOAT_NUMBER,

    /**
     * Represent of {@link java.lang.String} type.
     */
    STRING
}
