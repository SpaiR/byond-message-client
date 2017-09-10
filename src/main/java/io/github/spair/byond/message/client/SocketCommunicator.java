package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.communicator.*;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

class SocketCommunicator {

    private Socket socket;

    private DataOutputStream outputStream;
    private BufferedInputStream inputStream;

    private ServerAddress serverAddress;

    // Default timeout wait is 1 second.
    private int readTimeout = 1000;
    private boolean closeAfterSend;

    SocketCommunicator(ServerAddress serverAddress, int readTimeout, boolean closeAfterSend) {
        this.serverAddress = serverAddress;
        this.closeAfterSend = closeAfterSend;

        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        }
    }

    void sendToServer(byte[] bytes) throws HostUnavailableException, SendMessageException {
        try {
            openConnection();

            outputStream.write(bytes);
            outputStream.flush();

            if (closeAfterSend) {
                closeConnection();
            }
        } catch (HostUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new SendMessageException(e);
        }
    }


    ByteBuffer readFromServer() throws ReadResponseException {
        if (socket == null) {
            throw new ReadResponseException(
                    new RuntimeException("Connection to server isn't established. Reading is unavailable."));
        }

        if (readTimeout > 0) {
            return readWithTimeOut();
        } else {
            return simpleReadWithoutTimeOut();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private ByteBuffer readWithTimeOut() throws ReadResponseException {
        ByteBuffer responseBuffer = ByteBuffer.allocate(10000);

        try {
            // This try/catch block built over timeout exception logic, so no "break" operator in cycle.
            try {
                while (true) {
                    int inputByte = inputStream.read();
                    responseBuffer.put((byte) inputByte);
                }
            } catch (SocketTimeoutException ignored) {}
        } catch (Exception e) {
            throw new ReadResponseException(e);
        } finally {
            closeConnection();
        }

        return (ByteBuffer) responseBuffer.flip();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private ByteBuffer simpleReadWithoutTimeOut() throws ReadResponseException {
        ByteBuffer responseBuffer = ByteBuffer.allocate(0);

        try {
            // This try/catch block is to handle cases, when BYOND doesn't return anything.
            try {
                byte[] respInfo = new byte[5];
                inputStream.read(respInfo);

                int respSize = (ByteBuffer.wrap(new byte[]{respInfo[2], respInfo[3]}).getShort() - 1);

                byte[] respData = new byte[respSize];
                inputStream.read(respData);

                responseBuffer = ByteBuffer.allocate(respInfo.length + respData.length);
                responseBuffer.put(respInfo).put(respData);
            } catch (SocketTimeoutException ignored) {}
        } catch (Exception e) {
            throw new ReadResponseException(e);
        } finally {
            closeConnection();
        }

        return (ByteBuffer) responseBuffer.flip();
    }

    private void openConnection() throws HostUnavailableException, OpenConnectionException {
        try {
            socket = createSocket();
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new BufferedInputStream(socket.getInputStream());
        } catch (HostUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenConnectionException(e);
        }
    }

    private void closeConnection() throws CloseConnectionException {
        try {
            socket.close();
        } catch (Exception e) {
            throw new CloseConnectionException(e);
        }
    }

    private Socket createSocket() throws Exception {
        Socket socket;

        try {
            socket = new Socket(serverAddress.getName(), serverAddress.getPort());
            socket.setSoTimeout(readTimeout);
        } catch (ConnectException e) {
            throw new HostUnavailableException(
                    "Cannot to connect to host. Probably it's offline. Address: " +
                            serverAddress.getName() + ":" + serverAddress.getPort());
        } catch (UnknownHostException e) {
            throw new InvalidHostException(
                    "Unknown host to connect. Please, check entered host address and port", serverAddress);
        }

        return socket;
    }
}
