package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.HostUnavailableException;
import io.github.spair.byond.message.exception.UnexpectedResponseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ByondClientTest {

    private ByondClient byondClient;

    private static TestSocketServer serverSocket;

    private static ServerAddress VALID_ADDRESS;
    private static ServerAddress INVALID_ADDRESS;

    @BeforeClass
    public static void prepare() throws Exception {
        serverSocket = new TestSocketServer(9090);
        serverSocket.start();

        VALID_ADDRESS = new ServerAddress("127.0.0.1", 9090);
        INVALID_ADDRESS = new ServerAddress("127.0.0.1", 12345);
    }

    @Before
    public void setUp() {
        byondClient = new ByondClient();
    }

    @AfterClass
    public static void finish() throws Exception {
        serverSocket.stop();
    }

    @Test
    public void testSendCommand() {
        byondClient.sendCommand(new ByondMessage(VALID_ADDRESS, "test"));
    }

    @Test(expected = HostUnavailableException.class)
    public void testSendCommandWhenHostUnavailableException() {
        byondClient.sendCommand(new ByondMessage(INVALID_ADDRESS, "test"));
    }

    @Test
    public void testSendMessageWhenNumberResponse() {
        ByondResponse response = byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.NUMBER_REQUEST));

        assertEquals(TestSocketServer.NUMBER_VALUE, response.getResponse());
        assertEquals(ResponseType.FLOAT_NUMBER, response.getResponseType());
    }

    @Test
    public void testSendMessageWhenTextResponse() {
        ByondResponse response = byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST));

        assertEquals(TestSocketServer.TEXT_VALUE, response.getResponse());
        assertEquals(ResponseType.STRING, response.getResponseType());
    }

    @Test
    public void testSendMessageWhenNoneResponseExpected() {
        ByondResponse response = byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST, ResponseType.NONE));
        assertNull(response);
    }

    @Test(expected = UnexpectedResponseException.class)
    public void testSendMessageWhenEmptyResponse() {
        byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, "test", ResponseType.ANY));
    }

    @Test(expected = UnexpectedResponseException.class)
    public void testSendMessageWhenDifferentResponseType() {
        byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST, ResponseType.FLOAT_NUMBER));
    }

    @Test(expected = UnexpectedResponseException.class)
    public void testSendMessageWhenUnknownResponse() {
        byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.UNKNOWN_REQUEST));
    }

    @Test
    public void testSendMessageWithTimeout() {
        byondClient.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.NUMBER_REQUEST), 500);
    }
}