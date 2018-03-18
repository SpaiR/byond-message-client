package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.UnexpectedResponseException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("checkstyle:MagicNumber")
final class ByondResponseConverter {

    private ByondResponseConverter() {
    }

    static ByondResponse convertIntoResponse(final ByteBuffer byteBuffer) throws UnexpectedResponseException {
        if (byteBuffer.limit() > 0) {
            ByteBuffer sanitizedByteBuffer = sanitizeRawByteBuffer(byteBuffer);

            ResponseType actualResponseType = pullOutResponseType(byteBuffer);
            Object responseData = pullOutResponseData(sanitizedByteBuffer, actualResponseType);

            return new ByondResponse(responseData, actualResponseType);
        } else {
            throw new UnexpectedResponseException("Response length is zero when ResponseType isn't NONE.");
        }
    }

    private static ResponseType pullOutResponseType(final ByteBuffer data) throws UnexpectedResponseException {
        byte respTypeByte = data.get(4);

        switch (respTypeByte) {
            case 0x2a:
                return ResponseType.FLOAT_NUMBER;
            case 0x06:
                return ResponseType.STRING;
            default:
                throw new UnexpectedResponseException(
                        "Unknown response encoding byte. Should be '0x2a' or '0x06'. Found '" + respTypeByte + "'");
        }
    }

    private static Object pullOutResponseData(final ByteBuffer data, final ResponseType responseType) {
        byte[] responseBytes = data.array();

        if (responseType == ResponseType.FLOAT_NUMBER) {
            return createNumberTypeResponse(responseBytes);
        } else {
            return createStringTypeResponse(responseBytes);
        }
    }

    private static Float createNumberTypeResponse(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private static String createStringTypeResponse(final byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private static ByteBuffer sanitizeRawByteBuffer(final ByteBuffer rawBuffer) {
        int responseSize = rawBuffer.limit() - 5;
        return ByteBuffer.allocate(responseSize).put(rawBuffer.array(), 5, responseSize);
    }
}
