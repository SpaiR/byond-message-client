package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.UnexpectedResponseException;
import lombok.val;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static io.github.spair.byond.message.ByondClient.BYOND_CHARSET;

@SuppressWarnings("checkstyle:MagicNumber")
final class ByondResponseConverter {

    ByondResponse convertIntoResponse(final ByteBuffer byteBuffer) throws UnexpectedResponseException {
        if (byteBuffer.limit() > 0) {
            val sanitizedByteBuffer = sanitizeRawByteBuffer(byteBuffer);

            val actualResponseType = pullOutResponseType(byteBuffer);
            val responseData = pullOutResponseData(sanitizedByteBuffer, actualResponseType);

            return new ByondResponse(responseData, actualResponseType);
        } else {
            throw new UnexpectedResponseException("Response length is zero when ResponseType isn't NONE.");
        }
    }

    private ResponseType pullOutResponseType(final ByteBuffer data) throws UnexpectedResponseException {
        byte respTypeByte = data.get(4);

        switch (respTypeByte) {
            case 0x2a:
                return ResponseType.FLOAT_NUMBER;
            case 0x06:
                return ResponseType.STRING;
            default:
                throw new UnexpectedResponseException("Unknown response encoding byte. Should be '0x2a' or '0x06'. Found '" + respTypeByte + "'");
        }
    }

    private Object pullOutResponseData(final ByteBuffer data, final ResponseType responseType) {
        byte[] responseBytes = data.array();

        if (responseType == ResponseType.FLOAT_NUMBER) {
            return createNumberTypeResponse(responseBytes);
        } else {
            return createStringTypeResponse(responseBytes);
        }
    }

    private Float createNumberTypeResponse(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private String createStringTypeResponse(final byte[] bytes) {
        return new String(bytes, BYOND_CHARSET).trim();
    }

    private ByteBuffer sanitizeRawByteBuffer(final ByteBuffer rawBuffer) {
        int responseSize = rawBuffer.limit() - 5;
        return ByteBuffer.allocate(responseSize).put(rawBuffer.array(), 5, responseSize);
    }
}
