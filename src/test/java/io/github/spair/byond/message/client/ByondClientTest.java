package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ByondClientTest {

    private static TestSocketServer serverSocket;

    private static ServerAddress VALID_ADDRESS;
    private static ServerAddress INVALID_ADDRESS;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        serverSocket = new TestSocketServer(9090);
        serverSocket.start();

        VALID_ADDRESS = new ServerAddress("127.0.0.1", 9090);
        INVALID_ADDRESS = new ServerAddress("127.0.0.1", 12345);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        serverSocket.stop();
    }

    @Test
    public void testCreate() {
        ByondClient client = ByondClient.create();

        assertNotNull(client);
        assertEquals(ByondClient.class, client.getClass());
    }

    @Test
    public void testSendCommand() throws Exception {
        ByondClient client = new ByondClient();
        client.sendCommand(new ByondMessage(VALID_ADDRESS, "test"));

    }

    @Test(expected = HostUnavailableException.class)
    public void testSendCommand_HostUnavailableException() {
        ByondClient client = new ByondClient();
        client.sendCommand(new ByondMessage(INVALID_ADDRESS, "test"));
    }

    @Test
    public void testSendMessage_NumberResponse() throws Exception {
        ByondClient client = new ByondClient();
        ByondResponse response = client.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.NUMBER_REQUEST));

        assertEquals(TestSocketServer.NUMBER_VALUE, response.getResponseData());
        assertEquals(ResponseType.FLOAT_NUMBER, response.getResponseType());
    }

    @Test
    public void testSendMessage_TextResponse() {
        ByondClient client = new ByondClient();
        ByondResponse response = client.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST));

        assertEquals(TestSocketServer.TEXT_VALUE, response.getResponseData());
        assertEquals(ResponseType.STRING, response.getResponseType());
    }

    @Test
    public void testSendMessage_NoneResponse() {
        ByondClient client = new ByondClient();
        ByondResponse response = client.sendMessage(
                new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST, ResponseType.NONE));

        assertNull(response);
    }

    @Test(expected = EmptyResponseException.class)
    public void testSendMessage_EmptyResponseException() {
        ByondClient client = new ByondClient();
        client.sendMessage(new ByondMessage(VALID_ADDRESS, "test", ResponseType.ANY));
    }

    @Test(expected = UnexpectedResponseTypeException.class)
    public void testSendMessage_UnexpectedResponseTypeException() {
        ByondClient client = new ByondClient();
        client.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.TEXT_REQUEST, ResponseType.FLOAT_NUMBER));
    }

    @Test(expected = UnknownResponseException.class)
    public void testSendMessage_UnknownResponseException() {
        ByondClient client = new ByondClient();
        client.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.UNKNOWN_REQUEST));
    }

    @Test
    public void testSendMessage_WithTimeout() {
        ByondClient client = new ByondClient();
        client.sendMessage(new ByondMessage(VALID_ADDRESS, TestSocketServer.NUMBER_REQUEST), 500);
    }

    @Test
    public void testEnsureMessageIsTopic() throws Exception {
        ByondClient client = new ByondClient();

        ByondMessage controlMessage = new ByondMessage(null, 0, "?ping");

        ByondMessage testMessage_1 = new ByondMessage(null, 0, "ping");
        ByondMessage testMessage_2 = new ByondMessage(null, 0, "?ping");

        Method method = ByondClient.class.getDeclaredMethod("ensureMessageIsTopic", ByondMessage.class);
        method.setAccessible(true);

        method.invoke(client, testMessage_1);
        method.invoke(client, testMessage_2);

        assertEquals(controlMessage, testMessage_1);
        assertEquals(controlMessage, testMessage_2);
    }

    @Test
    public void testValidateResponseType() throws Exception {
        ByondClient client = new ByondClient();

        Method method = ByondClient.class.getDeclaredMethod("validateResponseType", ResponseType.class, ResponseType.class);
        method.setAccessible(true);

        method.invoke(client, ResponseType.FLOAT_NUMBER, ResponseType.FLOAT_NUMBER);
    }

    @Test
    public void testValidateResponseType_WithAnyType() throws Exception {
        ByondClient client = new ByondClient();

        Method method = ByondClient.class.getDeclaredMethod("validateResponseType", ResponseType.class, ResponseType.class);
        method.setAccessible(true);

        method.invoke(client, ResponseType.ANY, ResponseType.FLOAT_NUMBER);
    }

    @Test(expected = UnexpectedResponseTypeException.class)
    public void testValidateResponseType_UnexpectedResponseTypeException() throws Exception {
        ByondClient client = new ByondClient();

        Method method = ByondClient.class.getDeclaredMethod("validateResponseType", ResponseType.class, ResponseType.class);
        method.setAccessible(true);

        try {
            method.invoke(client, ResponseType.FLOAT_NUMBER, ResponseType.STRING);
        } catch (InvocationTargetException e) {
            throw (UnexpectedResponseTypeException) e.getCause();
        }
    }
}