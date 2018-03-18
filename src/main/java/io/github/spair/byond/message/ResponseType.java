package io.github.spair.byond.message;

/**
 * Represents what type of response expected from BYOND,
 * when used in {@link io.github.spair.byond.message.ByondMessage}
 * and actual returned data type in {@link ByondResponse}.
 */
public enum ResponseType {

    /** No response is expected, so {@link ByondClient#sendMessage} will return null. */
    NONE,

    /** Any type of data is expected. */
    ANY,

    /** Representation of {@link java.lang.Float} type. */
    FLOAT_NUMBER,

    /** Representation of {@link java.lang.String} type. */
    STRING
}
