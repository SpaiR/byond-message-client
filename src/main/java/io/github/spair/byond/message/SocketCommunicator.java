package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.HostUnavailableException;
import io.github.spair.byond.message.exception.CommunicationException;
import io.github.spair.byond.message.exception.SendMessageException;
import io.github.spair.byond.message.exception.ReadResponseException;
import io.github.spair.byond.message.exception.InvalidHostException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

class SocketCommunicator {

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ServerAddress serverAddress;

    private int readTimeout;
    private boolean shouldReadResponse;

    // Default timeout is 1 second or 1000 ms.
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final int DEFAULT_RESPONSE_SIZE = 10000;

    SocketCommunicator(final ServerAddress serverAddress, final int readTimeout, final boolean shouldReadResponse) {
        this.serverAddress = serverAddress;
        this.readTimeout = readTimeout;
        this.shouldReadResponse = shouldReadResponse;
    }

    ByteBuffer communicate(final byte[] bytes) throws HostUnavailableException, CommunicationException {
        try {
            try {
                openConnection();
                sendToServer(bytes);
                return shouldReadResponse ? readFromServer() : null;
            } finally {
                closeConnection();
            }
        } catch (HostUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }

    private void sendToServer(final byte[] bytes) throws SendMessageException {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (Exception e) {
            throw new SendMessageException(e);
        }
    }


    private ByteBuffer readFromServer() throws ReadResponseException {
        try {
            return readTimeout > 0 ? readWithTimeout() : readWithoutTimeout();
        } catch (Exception e) {
            throw new ReadResponseException(e);
        }
    }

    private ByteBuffer readWithTimeout() throws Exception {
        ByteBuffer responseBuffer = ByteBuffer.allocate(DEFAULT_RESPONSE_SIZE);

        try {
            while (true) {
                int inputByte = inputStream.read();

                if (inputByte == -1) {
                    break;
                }

                responseBuffer.put((byte) inputByte);
            }
        } catch (SocketTimeoutException ignored) {
        }

        return (ByteBuffer) responseBuffer.flip();
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "ResultOfMethodCallIgnored"})
    private ByteBuffer readWithoutTimeout() throws Exception {
        ByteBuffer responseBuffer = ByteBuffer.allocate(0);

        // This try/catch block is to handle cases, when BYOND doesn't return anything.
        // We ignoring the exception, because after it happened zero length byte buffer will be returned.
        // It will result into 'UnexpectedResponseException' later, so this is fine.
        try {
            byte[] respInfo = new byte[5];
            inputStream.read(respInfo);

            int respSize = (ByteBuffer.wrap(new byte[]{respInfo[2], respInfo[3]}).getShort() - 1);

            if (respSize > 0) {
                byte[] respData = new byte[respSize];
                inputStream.read(respData);

                responseBuffer = ByteBuffer.allocate(respInfo.length + respData.length);
                responseBuffer.put(respInfo).put(respData);
            }
        } catch (SocketTimeoutException ignored) {
        }

        return (ByteBuffer) responseBuffer.flip();
    }

    private void openConnection() throws Exception {
        createSocket();
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    private void closeConnection() throws Exception {
        if (socket != null) {
            socket.close();
        }
    }

    private void createSocket() throws Exception {
        try {
            socket = new Socket(serverAddress.getName(), serverAddress.getPort());
            socket.setSoTimeout(readTimeout > 0 ? readTimeout : DEFAULT_TIMEOUT);
        } catch (ConnectException e) {
            throw new HostUnavailableException(
                    "Can't connect to host. Probably it's offline. Address: "
                            + serverAddress.getName() + ":" + serverAddress.getPort());
        } catch (UnknownHostException e) {
            throw new InvalidHostException(
                    "Unknown host to connect. Please, check entered host address and port", serverAddress);
        }
    }
}
