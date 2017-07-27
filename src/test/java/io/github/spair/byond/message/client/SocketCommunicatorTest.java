package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.communicator.ReadResponseException;
import io.github.spair.byond.message.client.exceptions.communicator.SendMessageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class SocketCommunicatorTest {

    private ServerSocket serverSocket;

    @Before
    public void setUp() {
        try {
            serverSocket = new ServerSocket(9090, 0, InetAddress.getByName(null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        serverSocket.close();
    }

    @Test
    public void testSendToServer() {
        SocketCommunicator communicator = new SocketCommunicator(
                new ServerAddress("127.0.0.1", 9090), 1, false);

        communicator.sendToServer(new byte[10]);
    }

    @Test(expected = HostUnavailableException.class)
    public void testSendToServer_HostUnavailable() {
        SocketCommunicator communicator = new SocketCommunicator(
                new ServerAddress("127.0.0.1", 8080), 1, false);

        communicator.sendToServer(new byte[10]);
    }

    @Test(expected = SendMessageException.class)
    public void testSendToServer_SendMessage() {
        SocketCommunicator communicator = new SocketCommunicator(
                new ServerAddress("999.999.999.999", 9090), 1, false);

        communicator.sendToServer(new byte[10]);
    }

    @Test
    public void testReadFromServer() {
        SocketCommunicator communicator = new SocketCommunicator(
                new ServerAddress("127.0.0.1", 9090), 100, false);
        communicator.sendToServer(new byte[10]);  // To open connection.

        ByteBuffer controlBuffer = (ByteBuffer) ByteBuffer.allocate(10000).limit(0);
        ByteBuffer responseBuffer = communicator.readFromServer();

        assertEquals(controlBuffer, responseBuffer);
    }

    @Test(expected = ReadResponseException.class)
    public void testReadFromServer_WithoutConnection() {
        SocketCommunicator communicator = new SocketCommunicator(
                new ServerAddress("127.0.0.1", 9090), 100, false);

        communicator.readFromServer();
    }
}