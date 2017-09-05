package io.github.spair.byond.message.client;

class ByteArrayConverter {

    byte[] convertIntoBytes(String message) {
        return parseMessage(message);
    }

    private byte[] parseMessage(String message) {
        final char messageSize = (char) (message.getBytes().length + 6);

        final char[] firstPart = new char[] {0x00, 0x83, 0x00, messageSize, 0x00, 0x00, 0x00, 0x00, 0x00};
        final char[] messagePart = message.toCharArray();
        final char[] lastPart = new char[] {0x00};

        return convertToByteArray(concatAllParts(firstPart, messagePart, lastPart));
    }

    private char[] concatAllParts(char[]... arrays) {
        int resultLength = 0;
        for (char[] array : arrays) {
            resultLength += array.length;
        }

        final char[] result = new char[resultLength];

        int offset = 0;
        for (char[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    private byte[] convertToByteArray(char[] charArray) {
        final byte[] result = new byte[charArray.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) charArray[i];
        }

        return result;
    }
}
