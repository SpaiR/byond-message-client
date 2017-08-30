package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;

/**
 * Class to send string messages from Java program to BYOND server.
 * <br>
 * Could be created by no args constructor or with {@link ByondClient#create()} method.
 * <br><br>
 * Simple usage example:
 * <pre><code>
 * ByondMessage messageToSend = new ByondMessage(new ServerAddress("bagil.game.tgstation13.org", 2337), "?ping");
 *
 * ByondClient client = new ByondClient();
 * ByondResponse response = client.sendMessage(messageToSend);
 * </code></pre>
 *
 * Sending process could be made in one line style:
 * <pre><code>
 *  ByondResponse response = ByondClient.create().sendMessage(messageToSend);
 * </code></pre>
 *
 * Message sending is based on {@link java.net.Socket} class and process of reading returned data is built on
 * reacting on {@link java.net.SocketTimeoutException}, due to BYOND doesn't send 'end byte'.
 * <br>
 * Response reading timeout could be set up. If it won't, default value, which is <b>500 ms</b>, will be used.
 */
public class ByondClient {

    private final MessageConverter messageConverter = new MessageConverter();
    private final int READ_TIMEOUT = 500;

    /**
     * Static method create {@link io.github.spair.byond.message.client.ByondClient} instance
     * without constructor.
     *
     * @return new {@link io.github.spair.byond.message.client.ByondClient} instance.
     */
    public static ByondClient create() {
        return new ByondClient();
    }

    /**
     * Send message to BYOND server without waiting for response.
     *
     * @param byondMessage message to send.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     */
    public void sendCommand(ByondMessage byondMessage) throws HostUnavailableException {
        byondMessage.setExpectedResponse(ResponseType.NONE);
        sendMessage(byondMessage, READ_TIMEOUT);
    }

    /**
     * Send message to BYOND server with waiting for response.
     * Similar to {@link #sendMessage(ByondMessage, int)} but without second argument,
     * so as reading timeout <b>500 ms</b> used.
     *
     * @param byondMessage mesasge to send.
     * @return response from BYOND server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Cause could be in
     * in BYOND server itself. Typical, it doesn't handle message which was send and, as a result, not provide any response.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException {
        return sendMessage(byondMessage, READ_TIMEOUT);
    }

    /**
     * Send message to BYOND server with waiting for response.
     *
     * @param byondMessage message to send.
     * @param readTimeout timeout time to read response.
     * @return response from BYOND server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Cause could be in
     * in BYOND server itself. Typical, it doesn't handle message which was send and, as a result, not provide any response.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage, int readTimeout)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException {
        boolean closeCommAfterSend = (byondMessage.getExpectedResponse() == ResponseType.NONE);
        SocketCommunicator comm = new SocketCommunicator(byondMessage.getServerAddress(), readTimeout, closeCommAfterSend);

        comm.sendToServer(messageConverter.convertIntoBytes(byondMessage.getMessage()));

        if (closeCommAfterSend) {
            return null;
        } else {
            ByteBuffer rawServerResponse = comm.readFromServer();
            ByondResponse byondResponse = messageConverter.convertIntoResponse(rawServerResponse);

            validateResponseType(byondMessage.getExpectedResponse(), byondResponse.getResponseType());

            return byondResponse;
        }
    }

    private void validateResponseType(ResponseType expected, ResponseType actual) throws UnexpectedResponseTypeException {
        if (expected != ResponseType.ANY && expected != actual) {
            throw new UnexpectedResponseTypeException(
                    "Actual response type doesn't equals to what expected. " +
                            "Expected: " + expected + ". Actual: " + actual);
        }
    }
}
