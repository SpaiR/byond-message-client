package io.github.spair.byond.message.client;

import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MessageConverterTest {

    @Test
    public void testConvertIntoBytes() {
        // Space Station 13
        byte[] controlByteArray = new byte[]{0, -125, 0, 22, 0, 0, 0, 0, 0, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51, 0};

        MessageConverter converter = new MessageConverter();
        assertArrayEquals(controlByteArray, converter.convertIntoBytes("Space Station 13"));
    }

    @Test
    public void testConvertIntoResponse() {
        MessageConverter converter = new MessageConverter();
        ByondResponse controlByondResponse = new ByondResponse(61.0f, ResponseType.FLOAT_NUMBER);

        // 61.0f with some additional nulls
        byte[] floatInBytes = new byte[]{0, -125, 0, 5, 42, 0, 0, 116, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        assertEquals(controlByondResponse, converter.convertIntoResponse(ByteBuffer.wrap(floatInBytes)));
    }
}