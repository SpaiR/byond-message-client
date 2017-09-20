package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.communicator.CommunicationException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.communicator.ReadResponseException;
import io.github.spair.byond.message.client.exceptions.communicator.SendMessageException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class SocketCommunicatorTest {

    private static TestSocketServer serverSocket;

    private static ServerAddress VALID_ADDRESS;
    private static ServerAddress INVALID_PORT_ADDRESS;
    private static ServerAddress INVALID_IP_ADDRESS;

    // '23.0f' number encoded into BYOND format.
    private static final byte[] ENCODED_NUMBER = new byte[]{0, -125, 0, 13, 0, 0, 0, 0, 0, 63, 110, 117, 109, 98, 101, 114, 0};

    @BeforeClass
    public static void prepare() throws Exception {
        serverSocket = new TestSocketServer(10101);
        serverSocket.start();

        VALID_ADDRESS = new ServerAddress("127.0.0.1", 10101);
        INVALID_PORT_ADDRESS = new ServerAddress("127.0.0.1", 12345);
        INVALID_IP_ADDRESS = new ServerAddress("999.999.999.999", 10101);
    }

    @AfterClass
    public static void finish() throws Exception {
        serverSocket.stop();
    }

    @Test
    public void testCommunicate_WithoutTimeout() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, false);
        ByteBuffer responseBuffer = communicator.communicate(ENCODED_NUMBER);

        assertArrayEquals(TestSocketServer.NUMBER_RESPONSE, responseBuffer.array());
    }

    @Test
    public void testCommunicate_WithTimeout() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 500, false);
        ByteBuffer rawResponseBuffer = communicator.communicate(ENCODED_NUMBER);

        ByteBuffer responseBuffer = ByteBuffer.allocate(rawResponseBuffer.limit());
        responseBuffer.put(rawResponseBuffer.array(), 0, 9);

        assertArrayEquals(TestSocketServer.NUMBER_RESPONSE, responseBuffer.array());
    }

    @Test
    public void testCommunicate_WithCloseAfterSend() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, true);
        assertNull(communicator.communicate(ENCODED_NUMBER));
    }

    @Test(expected = CommunicationException.class)
    public void testCommunicate_CommunicationException() {
        SocketCommunicator communicator = new SocketCommunicator(INVALID_IP_ADDRESS, 0, false);
        communicator.communicate(ENCODED_NUMBER);
    }

    @Test(expected = HostUnavailableException.class)
    public void testCommunicate_HostUnavailableException() {
        SocketCommunicator communicator = new SocketCommunicator(INVALID_PORT_ADDRESS, 0, false);
        communicator.communicate(ENCODED_NUMBER);
    }

    @Test(expected = SendMessageException.class)
    public void testSendToServer_WithoutConnection() throws Exception {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, false);

        Method method = SocketCommunicator.class.getDeclaredMethod("sendToServer", byte[].class);
        method.setAccessible(true);

        try {
            method.invoke(communicator, (Object) ENCODED_NUMBER);
        } catch (InvocationTargetException e) {
            throw (SendMessageException) e.getCause();
        }
    }

    @Test(expected = ReadResponseException.class)
    public void testReadFromServer_WithoutConnection() throws Exception {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, false);

        Method method = SocketCommunicator.class.getDeclaredMethod("readFromServer");
        method.setAccessible(true);

        try {
            method.invoke(communicator);
        } catch (InvocationTargetException e) {
            throw (ReadResponseException) e.getCause();
        }
    }
}