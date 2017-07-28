package io.github.spair.byond.message.response;

public enum ResponseType {
    /**
     * No data is expected.
     */
    NONE,
    /**
     * Data of any type is expected.
     */
    ANY,
    /**
     * Only data of {@link java.lang.Float} type is expected.
     */
    FLOAT_NUMBER,
    /**
     * Only data of {@link java.lang.String} type is expected.
     */
    STRING
}
