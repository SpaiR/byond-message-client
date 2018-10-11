package io.github.spair.byond.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for converted BYOND response.<br>
 * Response represented as {@link java.lang.Object} class, so type cast is necessary.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("WeakerAccess")
public class ByondResponse {

    private Object response;
    private ResponseType responseType;

    public <T> T getResponse(final Class<T> clazz) {
        return clazz.cast(response);
    }
}
