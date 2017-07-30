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

/**
 * Class to handle all communication things with {@link java.net.Socket}.
 */
class SocketCommunicator {

    private Socket socket;

    private DataOutputStream outputStream;
    private BufferedInputStream inputStream;

    private ServerAddress serverAddress;

    private int readTimeout;
    private boolean closeAfterSend;

    SocketCommunicator(ServerAddress serverAddress, int readTimeout, boolean closeAfterSend) {
        this.serverAddress = serverAddress;
        this.readTimeout = readTimeout;
        this.closeAfterSend = closeAfterSend;
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

    /**
     * Response bytes are stored in {@link java.nio.ByteBuffer} object.
     * Capacity of it is 10000 bytes to be sure, that everything can be stored.
     * <br>
     * Before returning method {@link ByteBuffer#flip()} is invoked to prepare object and change {@link java.nio.ByteBuffer#limit} value.
     * <br>
     * Connection closed during {@link java.net.SocketTimeoutException} because there is no way to know,
     * when BYOND ended to send response.
     * @return {@link java.nio.ByteBuffer} with response data and additional empty space.
     */
    ByteBuffer readFromServer() throws ReadResponseException {
        ByteBuffer rawResponseByteBuffer = ByteBuffer.allocate(10000);

        try {
            try {
                while (true) {
                    int inputByte = inputStream.read();

                    if (inputByte == -1) {
                        break;
                    }

                    rawResponseByteBuffer.put((byte) inputByte);
                }
            } catch (SocketTimeoutException e) {
                closeConnection();
            }
        } catch (Exception e) {
            throw new ReadResponseException(e);
        }

        return (ByteBuffer) rawResponseByteBuffer.flip();
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