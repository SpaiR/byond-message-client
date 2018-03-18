package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.CommunicationException;
import io.github.spair.byond.message.exception.HostUnavailableException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
    public void testCommunicateWithoutTimeout() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, true);
        ByteBuffer responseBuffer = communicator.communicate(ENCODED_NUMBER);

        assertArrayEquals(TestSocketServer.NUMBER_RESPONSE, responseBuffer.array());
    }

    @Test
    public void testCommunicateWithTimeout() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 500, true);
        ByteBuffer rawResponseBuffer = communicator.communicate(ENCODED_NUMBER);

        ByteBuffer responseBuffer = ByteBuffer.allocate(rawResponseBuffer.limit());
        responseBuffer.put(rawResponseBuffer.array(), 0, 9);

        assertArrayEquals(TestSocketServer.NUMBER_RESPONSE, responseBuffer.array());
    }

    @Test
    public void testCommunicateWithCloseAfterSend() {
        SocketCommunicator communicator = new SocketCommunicator(VALID_ADDRESS, 0, false);
        assertNull(communicator.communicate(ENCODED_NUMBER));
    }

    @Test(expected = CommunicationException.class)
    public void testCommunicateCommunicationException() {
        SocketCommunicator communicator = new SocketCommunicator(INVALID_IP_ADDRESS, 0, false);
        communicator.communicate(ENCODED_NUMBER);
    }

    @Test(expected = HostUnavailableException.class)
    public void testCommunicateHostUnavailableException() {
        SocketCommunicator communicator = new SocketCommunicator(INVALID_PORT_ADDRESS, 0, false);
        communicator.communicate(ENCODED_NUMBER);
    }
}