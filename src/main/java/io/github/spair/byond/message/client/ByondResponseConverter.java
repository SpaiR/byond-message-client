package io.github.spair.byond.message.client;

import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class ByondResponseConverter {

    ByondResponse convertIntoResponse(ByteBuffer byteBuffer) throws EmptyResponseException, UnknownResponseException {
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

    private Float createNumberTypeResponse(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private String createStringTypeResponse(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append((char) b);
        }

        return sb.toString();
    }

    private ByteBuffer sanitizeRawByteBuffer(ByteBuffer rawBuffer) {
        int responseSize = rawBuffer.limit();
        ByteBuffer sanitizedBuffer = ByteBuffer.allocate(responseSize - 5);

        for (int i = 5; i < responseSize; i++) {
            sanitizedBuffer.put(rawBuffer.get(i));
        }

        return sanitizedBuffer;
    }
}
