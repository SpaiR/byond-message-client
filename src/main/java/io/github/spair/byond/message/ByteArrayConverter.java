package io.github.spair.byond.message;

import static io.github.spair.byond.message.ByondClient.BYOND_CHARSET;

@SuppressWarnings("checkstyle:MagicNumber")
final class ByteArrayConverter {

    byte[] convertIntoBytes(final String textMessage) {
        byte[] message = textMessage.getBytes(BYOND_CHARSET);
        char messageSize = (char) (message.length + 6);

        byte[] prefix = toBytes(new char[]{0x00, 0x83, 0x00, messageSize, 0x00, 0x00, 0x00, 0x00, 0x00});
        byte[] suffix = toBytes(new char[]{0x00});

        return concatAllParts(prefix, message, suffix);
    }

    private byte[] concatAllParts(final byte[] prefix, final byte[] message, final byte[] suffix) {
        byte[] result = new byte[prefix.length + message.length + suffix.length];

        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(message, 0, result, prefix.length, message.length);
        System.arraycopy(suffix, 0, result, prefix.length + message.length, suffix.length);

        return result;
    }

    private byte[] toBytes(final char[] chars) {
        byte[] result = new byte[chars.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) chars[i];
        }

        return result;
    }
}
