package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.UnexpectedResponseException;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ByondResponseConverterTest {

    private final ByondResponseConverter converter = new ByondResponseConverter();

    // 57.0f
    private final byte[] floatInBytes = new byte[]{0, -125, 0, 5, 42, 0, 0, 100, 66, 0, 0, 0, 0};
    // Space Station 13
    private final byte[] stringInBytes = new byte[]{0, -125, 4, 90, 6, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51};

    @Test
    public void testConvertIntoResponseWithFloat() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(floatInBytes);
        ByondResponse expectedByondResponse = new ByondResponse(57.0f, ResponseType.FLOAT_NUMBER);

        assertEquals(expectedByondResponse, converter.convertIntoResponse(byteBuffer));
    }

    @Test
    public void testConvertIntoResponseWithString() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(stringInBytes);
        ByondResponse expectedByondResponse = new ByondResponse("Space Station 13", ResponseType.STRING);

        assertEquals(expectedByondResponse, converter.convertIntoResponse(byteBuffer));
    }

    @Test(expected = UnexpectedResponseException.class)
    public void testConvertIntoResponseWhenEmptyResponse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[0]);
        converter.convertIntoResponse(byteBuffer);
    }

    @Test(expected = UnexpectedResponseException.class)
    public void testConvertIntoResponseWhenUnknownResponse() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5});
        converter.convertIntoResponse(byteBuffer);
    }
}