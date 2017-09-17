package io.github.spair.byond.message.client;

import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

class ByondResponseConverter {

    static ByondResponse convertIntoResponse(ByteBuffer byteBuffer) throws EmptyResponseException, UnknownResponseException {
        if (byteBuffer.limit() > 0) {
            ByteBuffer sanitizedByteBuffer = sanitizeRawByteBuffer(byteBuffer);

            ResponseType actualResponseType = pullOutActualResponseType(byteBuffer);
            Object responseData = pullOutResponseData(sanitizedByteBuffer, actualResponseType);

            return new ByondResponse(responseData, actualResponseType);
        } else {
            throw new EmptyResponseException("Response length is zero when ResponseType is not NONE.");
        }
    }

    private static ResponseType pullOutActualResponseType(ByteBuffer data) throws UnknownResponseException {
        byte respTypeByte = data.get(4);

        switch (respTypeByte) {
            case 0x2a:
                return ResponseType.FLOAT_NUMBER;
            case 0x06:
                return ResponseType.STRING;
            default:
                throw new UnknownResponseException(
                        "Unknown response encoding byte. Should be '0x2a' or '0x06'. Found '" + respTypeByte + "'");
        }
    }

    private static Object pullOutResponseData(ByteBuffer data, ResponseType responseType) {
        byte[] responseBytes = data.array();

        switch (responseType) {
            case FLOAT_NUMBER:
                return createNumberTypeResponse(responseBytes);
            case STRING:
                return createStringTypeResponse(responseBytes);
            default:
                return null;
        }
    }

    private static Float createNumberTypeResponse(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private static String createStringTypeResponse(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private static ByteBuffer sanitizeRawByteBuffer(ByteBuffer rawBuffer) {
        int responseSize = rawBuffer.limit() - 5;
        return ByteBuffer.allocate(responseSize).put(rawBuffer.array(), 5, responseSize);
    }
}
