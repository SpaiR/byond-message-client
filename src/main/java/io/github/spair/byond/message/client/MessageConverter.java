package io.github.spair.byond.message.client;

import io.github.spair.byond.message.client.exceptions.converter.ConvertMessageException;
import io.github.spair.byond.message.client.exceptions.converter.ConvertResponseException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.response.ByondResponse;

import java.nio.ByteBuffer;

/**
 * Container for {@link io.github.spair.byond.message.client.ByteArrayConverter}
 * and {@link io.github.spair.byond.message.client.ByondResponseConverter}.
 */
class MessageConverter {

    private final ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
    private final ByondResponseConverter byondResponseConverter = new ByondResponseConverter();

    byte[] convertIntoBytes(String message) throws ConvertMessageException {
        byte[] bytes;

        try {
            bytes = byteArrayConverter.convert(message);
        } catch (Exception e) {
            throw new ConvertMessageException(e);
        }

        return bytes;
    }

    ByondResponse convertIntoResponse(ByteBuffer rawServerResponse)
            throws EmptyResponseException, ConvertResponseException {
        ByondResponse byondResponse;

        try {
            byondResponse = byondResponseConverter.convert(rawServerResponse);
        } catch (EmptyResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ConvertResponseException(e);
        }
        return byondResponse;
    }
}
