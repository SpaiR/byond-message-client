package io.github.spair.byond.message.client;

import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ByondResponseConverterTest {

    // 57.0f
    private final byte[] floatInBytes = new byte[]{0, -125, 0, 5, 42, 0, 0, 100, 66, 0, 0, 0, 0};
    // Space Station 13
    private final byte[] stringInBytes = new byte[]{0, -125, 4, 90, 6, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51};

    @Test
    public void testConvertIntoResponse_WithFloat() {
        ByondResponseConverter converter = new ByondResponseConverter();

        ByteBuffer byteBuffer = ByteBuffer.wrap(floatInBytes);
        ByondResponse expectedByondResponse = new ByondResponse(57.0f, ResponseType.FLOAT_NUMBER);

        assertEquals(expectedByondResponse, converter.convertIntoResponse(byteBuffer));
    }

    @Test
    public void testConvertIntoResponse_WithString() {
        ByondResponseConverter converter = new ByondResponseConverter();

        ByteBuffer byteBuffer = ByteBuffer.wrap(stringInBytes);
        ByondResponse expectedByondResponse = new ByondResponse("Space Station 13", ResponseType.STRING);

        assertEquals(expectedByondResponse, converter.convertIntoResponse(byteBuffer));
    }

    @Test(expected = EmptyResponseException.class)
    public void testConvertIntoResponse_EmptyResponseException() {
        ByondResponseConverter converter = new ByondResponseConverter();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[0]);

        converter.convertIntoResponse(byteBuffer);
    }

    @Test(expected = UnknownResponseException.class)
    public void testConvertIntoResponse_UnknownResponseException() {
        ByondResponseConverter converter = new ByondResponseConverter();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5});

        converter.convertIntoResponse(byteBuffer);
    }

    @Test
    public void testSanitizeRawBuffer() throws Exception {
        ByondResponseConverter converter = new ByondResponseConverter();

        Method method = ByondResponseConverter.class.getDeclaredMethod("sanitizeRawByteBuffer", ByteBuffer.class);
        method.setAccessible(true);

        ByteBuffer rawBuffer = ByteBuffer.allocate(15);

        for (int i = 1; i <= 10; i++) {
            rawBuffer.put((byte) i);
        }

        rawBuffer.flip();

        ByteBuffer sanitizedBuffer = (ByteBuffer) method.invoke(converter, rawBuffer);

        assertEquals(15, rawBuffer.capacity());
        assertEquals(5, sanitizedBuffer.capacity());
    }
}