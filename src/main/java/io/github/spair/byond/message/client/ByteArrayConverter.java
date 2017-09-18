package io.github.spair.byond.message.client;

class ByteArrayConverter {

    static byte[] convertIntoBytes(String message) {
        char messageSize = (char) (message.getBytes().length + 6);

        char[] firstPart = new char[] {0x00, 0x83, 0x00, messageSize, 0x00, 0x00, 0x00, 0x00, 0x00};
        char[] messagePart = message.toCharArray();
        char[] lastPart = new char[] {0x00};

        return convertToByteArray(concatAllParts(firstPart, messagePart, lastPart));
    }

    private static char[] concatAllParts(char[] first, char[] message, char[] last) {
        char[] result = new char[first.length + message.length + last.length];

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(message, 0, result, first.length, message.length);
        System.arraycopy(last, 0, result, first.length + message.length, last.length);

        return result;
    }

    private static byte[] convertToByteArray(char[] charArray) {
        byte[] result = new byte[charArray.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) charArray[i];
        }

        return result;
    }
}
