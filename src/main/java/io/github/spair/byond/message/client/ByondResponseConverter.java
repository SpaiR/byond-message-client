package io.github.spair.byond.message.client;

import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Converter from {@link java.nio.ByteBuffer} to {@link io.github.spair.byond.message.response.ByondResponse}.
 * <br>
 * Some methods like {@link #pullOutActualResponseType(ByteBuffer)} may look strange and hardcoded,
 * but it's only way to convert BYOND response byte array into readable information.
 */
class ByondResponseConverter {

    /**
     * Convert method.
     * @param byteBuffer byte buffer to convert into response.
     * @return {@link io.github.spair.byond.message.response.ByondResponse} from byte buffer.
     * @throws EmptyResponseException signals that response was empty.
     * @throws UnknownResponseException thrown if response has unknown format and can't be converted.
     */
    ByondResponse convert(ByteBuffer byteBuffer) throws EmptyResponseException, UnknownResponseException {
        Object responseData;
        ResponseType actualResponseType;

        if (byteBuffer.limit() > 0) {
            ByteBuffer sanitizedByteBuffer = sanitizeRawByteBuffer(byteBuffer);

            actualResponseType = pullOutActualResponseType(byteBuffer);
            responseData = pullOutResponseData(sanitizedByteBuffer, actualResponseType);
        } else {
            throw new EmptyResponseException("Response length is zero when ResponseType not NONE.");
        }

        return new ByondResponse(responseData, actualResponseType);
    }

    /**
     * Fourth byte in array has type determination of response.
     * @param data byte buffer to get type from.
     * @return {@link io.github.spair.byond.message.response.ResponseType} from given bytes.
     * @throws UnknownResponseException thrown if response has unknown format and can't be handled.
     */
    private ResponseType pullOutActualResponseType(ByteBuffer data) throws UnknownResponseException {
        ResponseType responseType;
        String responseHexString = Integer.toHexString(data.get(4));

        switch (responseHexString) {
            case "2a":
                responseType = ResponseType.FLOAT_NUMBER;
                break;
            case "6":
                responseType = ResponseType.STRING;
                break;
            default:
                throw new UnknownResponseException(
                        "Unknown response encoding. Should be '2a' or '6'. Found '" + responseHexString + "'");
        }

        return responseType;
    }

    /**
     * Method to convert bytes into response data.
     * @param data bytes with data.
     * @param responseType actual response type.
     * @return converted data.
     */
    private Object pullOutResponseData(ByteBuffer data, ResponseType responseType) {
        byte[] responseBytes = data.array();
        Object responseData = null;

        switch (responseType) {
            case FLOAT_NUMBER:
                responseData = createNumberTypeResponse(responseBytes);
                break;
            case STRING:
                responseData = createStringTypeResponse(responseBytes);
                break;
        }

        return responseData;
    }

    /**
     * BYOND always return numbers as {@link java.lang.Float}.
     * @param bytes bytes to convert.
     * @return converted number.
     */
    private Float createNumberTypeResponse(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    /**
     * {@link java.lang.String} object can be easily got with byte2char convertion.
     * @param bytes bytes with {@link java.lang.String} data.
     * @return converted data.
     */
    private String createStringTypeResponse(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append((char) b);
        }

        return sb.toString();
    }

    /**
     * Response bytes from BYOND have additional data.
     * Like first byte has response size. Fourth byte is response format. And so on.
     * This method convert these raw response into data-only bytes.
     * @param rawBuffer raw response.
     * @return data-only response.
     */
    private ByteBuffer sanitizeRawByteBuffer(ByteBuffer rawBuffer) {
        int responseSize = rawBuffer.limit();
        ByteBuffer sanitizedBuffer = ByteBuffer.allocate(responseSize - 5);

        for (int i = 5; i < responseSize; i++) {
            sanitizedBuffer.put(rawBuffer.get(i));
        }

        return sanitizedBuffer;
    }
}
