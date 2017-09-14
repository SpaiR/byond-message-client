package io.github.spair.byond.message.client;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ByteArrayConverterTest {

    @Test
    public void testConvertIntoBytes() {
        byte[] controlByteArray = new byte[]{0, -125, 0, 22, 0, 0, 0, 0, 0, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51, 0};  // Space Station 13

        assertArrayEquals(controlByteArray, ByteArrayConverter.convertIntoBytes("Space Station 13"));
    }
}