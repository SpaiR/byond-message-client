package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.communicator.*;

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
    private boolean closeAfterSend;

    // Default timeout is 1 second or 1000 ms.
    private static final int DEFAULT_TIMEOUT = 1000;

    SocketCommunicator(ServerAddress serverAddress, int readTimeout, boolean closeAfterSend) {
        this.serverAddress = serverAddress;
        this.readTimeout = readTimeout;
        this.closeAfterSend = closeAfterSend;
    }

    ByteBuffer communicate(byte[] bytes) throws HostUnavailableException, CommunicationException {
        try {
            try {
                openConnection();
                sendToServer(bytes);
                return closeAfterSend ? null : readFromServer();
            } finally {
                closeConnection();
            }
        } catch (HostUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
    }

    private void sendToServer(byte[] bytes) throws SendMessageException {
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (Exception e) {
            throw new SendMessageException(e);
        }
    }


    private ByteBuffer readFromServer() throws ReadResponseException {
        try {
            if (readTimeout > 0) {
                return readWithTimeout();
            } else {
                return simpleReadWithoutTimeout();
            }
        } catch (Exception e) {
            throw new ReadResponseException(e);
        }
    }

    private ByteBuffer readWithTimeout() throws Exception {
        ByteBuffer responseBuffer = ByteBuffer.allocate(10000);

        try {
            while (true) {
                int inputByte = inputStream.read();

                if (inputByte == -1) {
                    break;
                }

                responseBuffer.put((byte) inputByte);
            }
        } catch (SocketTimeoutException ignored) {}

        return (ByteBuffer) responseBuffer.flip();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private ByteBuffer simpleReadWithoutTimeout() throws Exception {
        ByteBuffer responseBuffer = ByteBuffer.allocate(0);

        // This try/catch block is to handle cases, when BYOND doesn't return anything.
        // We ignoring the exception, because after it happened zero length byte buffer will be returned.
        // It will result into 'EmptyResponseException' later, so this is fine.
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
        } catch (SocketTimeoutException ignored) {}

        return (ByteBuffer) responseBuffer.flip();
    }

    private void openConnection() throws Exception {
        socket = createSocket();
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    private void closeConnection() throws Exception {
        if (socket != null) {
            socket.close();
        }
    }

    private Socket createSocket() throws Exception {
        try {
            Socket socket = new Socket(serverAddress.getName(), serverAddress.getPort());
            socket.setSoTimeout(readTimeout > 0 ? readTimeout : DEFAULT_TIMEOUT);
            return socket;
        } catch (ConnectException e) {
            throw new HostUnavailableException(
                    "Cannot to connect to host. Probably it's offline. Address: " +
                            serverAddress.getName() + ":" + serverAddress.getPort());
        } catch (UnknownHostException e) {
            throw new InvalidHostException(
                    "Unknown host to connect. Please, check entered host address and port", serverAddress);
        }
    }
}
