package io.github.spair.byond.message.client;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;

public class ByteArrayConverterTest {

    @Test
    public void testConvertIntoBytes() {
        byte[] controlByteArray = new byte[]{0, -125, 0, 22, 0, 0, 0, 0, 0, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51, 0};  // Space Station 13

        assertArrayEquals(controlByteArray, ByteArrayConverter.convertIntoBytes("Space Station 13"));
    }

    @Test
    public void testConcatAllParts() throws Exception {
        ByteArrayConverter byteArrayConverter = new ByteArrayConverter();

        Method method = ByteArrayConverter.class.getDeclaredMethod("concatAllParts", char[].class, char[].class, char[].class);
        method.setAccessible(true);

        char[] controlArray = new char[]{'a', 'b', 'c', 'd', 'e', 'f'};
        char[] arrayToTest = (char[]) method.invoke(byteArrayConverter, new char[]{'a', 'b'}, new char[]{'c', 'd'}, new char[]{'e', 'f'});

        assertArrayEquals(controlArray, arrayToTest);
    }
}