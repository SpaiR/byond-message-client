package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.ServerAddress;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.response.ResponseType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.Assert.*;

public class ByondClientTest {

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
    public void testCreate() {
        ByondClient client = ByondClient.create();

        assertNotNull(client);
        assertEquals(ByondClient.class, client.getClass());
    }

    @Test
    public void testSendCommand() {
        ByondClient client = new ByondClient();
        client.sendCommand(new ByondMessage(new ServerAddress("127.0.0.1", 9090), "test"));
    }

    @Test(expected = HostUnavailableException.class)
    public void testSendCommand_HostUnavailable() {
        ByondClient client = new ByondClient();
        client.sendCommand(new ByondMessage(new ServerAddress("127.0.0.1", 8080), "test"));
    }

    @Test
    public void sendMessage() {
        ByondClient client = new ByondClient();
        assertNull(client.sendMessage(
                new ByondMessage(new ServerAddress("127.0.0.1", 9090), "test", ResponseType.NONE)));
    }

    @Test(expected = EmptyResponseException.class)
    public void sendMessage_EmptyResponse() {
        ByondClient client = new ByondClient();
        client.sendMessage(
                new ByondMessage(new ServerAddress("127.0.0.1", 9090), "test", ResponseType.ANY));
    }

    @Test
    public void testValidateResponseType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ByondClient client = new ByondClient();

        Method method = ByondClient.class.getDeclaredMethod("validateResponseType", ResponseType.class, ResponseType.class);
        method.setAccessible(true);

        method.invoke(client, ResponseType.FLOAT_NUMBER, ResponseType.FLOAT_NUMBER);
    }

    @Test
    public void testValidateResponseType_WithAnyType()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ByondClient client = new ByondClient();

        Method method = ByondClient.class.getDeclaredMethod("validateResponseType", ResponseType.class, ResponseType.class);
        method.setAccessible(true);

        method.invoke(client, ResponseType.ANY, ResponseType.FLOAT_NUMBER);
    }

    @Test(expected = UnexpectedResponseTypeException.class)
    public void testValidateResponseType_UnexpectedResponseType() throws NoSuchMethodException, IllegalAccessException {
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