package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
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
 * reacting on {@link java.net.SocketTimeoutException}, due to BYOND doesn't send end byte.
 */
public class ByondClient {

    private final ByondResponseConverter byondResponseConverter = new ByondResponseConverter();
    private final ByteArrayConverter byteArrayConverter = new ByteArrayConverter();

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
        sendMessage(byondMessage, 0);
    }

    /**
     * Send message to BYOND server with waiting for response.
     * <br>
     * It is blocking method. Waiting time creates from connection lag and sending/reading response time.
     * Unlike {@link ByondClient#sendMessage(ByondMessage, int)} additional wait from timeout won't be added,
     * but method still guarantee, that whole data will be got.
     *
     * @param byondMessage message to send.
     * @return response from BYOND server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Cause could be in
     * BYOND server itself. Typical, it doesn't handle message which was send and, as a result, not provide any response.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException, UnknownResponseException {
        return sendMessage(byondMessage, 0);
    }

    /**
     * Send message to BYOND server with waiting for response and custom timeout.
     * <br>
     * It is blocking method. Waiting time creates from connection lag, sending time and reading timeout.
     * If timeout expired before response fully read data will be incomplete, but still exist.
     * <br>
     * Zero and less timeout value means, that actual read will be performed like in {@link ByondClient#sendMessage(ByondMessage)},
     * without any timeout at all.
     * <br>
     * Method not recommended to be used, due to extra wait time from timeout,
     * and shredded data if timeout is to low.
     *
     * @param byondMessage message to send.
     * @param readTimeout timeout time to read response.
     * @return response from BYOND server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Cause could be in
     * BYOND server itself. Typical, it doesn't handle message which was send and, as a result, not provide any response.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage, int readTimeout)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException, UnknownResponseException
    {
        boolean closeCommAfterSend = (byondMessage.getExpectedResponse() == ResponseType.NONE);
        SocketCommunicator comm = new SocketCommunicator(byondMessage.getServerAddress(), readTimeout, closeCommAfterSend);

        ensureMessageIsTopic(byondMessage);
        comm.sendToServer(byteArrayConverter.convertIntoBytes(byondMessage.getMessage()));

        if (closeCommAfterSend) {
            return null;
        } else {
            ByteBuffer rawServerResponse = comm.readFromServer();
            ByondResponse byondResponse = byondResponseConverter.convertIntoResponse(rawServerResponse);

            validateResponseType(byondMessage.getExpectedResponse(), byondResponse.getResponseType());

            return byondResponse;
        }
    }

    private void ensureMessageIsTopic(ByondMessage message) {
        String messageText = message.getMessage();

        if (!messageText.startsWith("?")) {
            message.setMessage("?" + messageText);
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
