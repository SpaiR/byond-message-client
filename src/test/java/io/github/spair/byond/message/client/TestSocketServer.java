package io.github.spair.byond.message.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TestSocketServer {

    private ServerSocket serverSocket;
    private ExecutorService executorService;

    static final String NUMBER_REQUEST = "number";
    static final String TEXT_REQUEST = "text";
    static final String UNKNOWN_REQUEST = "unknown";

    static final Float NUMBER_VALUE = 23.0f;
    static final String TEXT_VALUE = "Space Station 13";

    // Byte arrays for responses in BYOND format.
    static final byte[] NUMBER_RESPONSE = new byte[]{0, -125, 0, 5, 42, 0, 0, -72, 65};  // 23.0f
    static final byte[] TEXT_RESPONSE = new byte[]{0, -125, 0, 18, 6, 83, 112, 97, 99, 101, 32, 83, 116, 97, 116, 105, 111, 110, 32, 49, 51, 0};  // Space Station 13

    TestSocketServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    void start() {
        executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();

                    char[] requestChars = readRequest(socket);
                    writeResponse(socket, requestChars);

                    socket.close();
                }
            } catch (SocketException ignored) {  // 'accept()' method will throw this if 'stop()' method called.
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    void stop() throws Exception {
        executorService.shutdownNow();
        serverSocket.close();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private char[] readRequest(Socket socket) throws IOException {
        char[] requestChars = new char[100];

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        reader.read(requestChars);

        return requestChars;
    }

    private void writeResponse(Socket socket, char[] requestChars) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(getResponse(requestChars));
        os.flush();
    }

    private byte[] getResponse(char[] chars) {
        // Actual response starts from 10 position.
        if (chars.length > 10) {
            String requestText = String.copyValueOf(chars, 10, chars.length - 10).trim();

            switch (requestText) {
                case NUMBER_REQUEST:
                    return NUMBER_RESPONSE;
                case TEXT_REQUEST:
                    return TEXT_RESPONSE;
                case UNKNOWN_REQUEST:
                    return new byte[]{1, 2, 3, 4, 5};
            }
        }

        return new byte[0];
    }
}
