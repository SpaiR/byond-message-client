package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;

/**
 * Class to send string messages from Java program to BYOND game server.
 * <br>
 * Because BYOND has no native REST or something like that, communication handled with {@link java.net.Socket}.
 * BYOND game servers didn't developed to handle external communication due sockets,
 * so while reading response data, there is no way to know where it's ended.
 * The only way to close socket connection is during {@link java.net.SocketTimeoutException}.
 * <br>
 * Default timeout wait is <b>500 ms</b>. This value can be controlled with {@link #sendMessage(ByondMessage, int)} method.
 */
public class ByondClient {

    private final MessageConverter messageConverter = new MessageConverter();
    private final int READ_TIMEOUT = 500;

    /**
     * Static method to do things instead of this:
     * <pre>
     *     <code>
     *         ByondClient client = new ByondClient();
     *         client.sendCommand({@link io.github.spair.byond.message.ByondMessage});
     *     </code>
     * </pre>
     * like that:
     * <pre>
     *     <code>
     *         ByondClient.create().sendCommand({@link io.github.spair.byond.message.ByondMessage});
     *     </code>
     * </pre>
     * @return new {@link io.github.spair.byond.message.client.ByondClient} instance.
     */
    public static ByondClient create() {
        return new ByondClient();
    }

    /**
     * Send message to BYOND game server without waiting for response.
     * @param byondMessage message to send.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     */
    public void sendCommand(ByondMessage byondMessage) throws HostUnavailableException {
        byondMessage.setExpectedResponse(ResponseType.NONE);
        sendMessage(byondMessage, READ_TIMEOUT);
    }

    /**
     * Send message to BYOND game server with getting response.
     * Similar to {@link #sendMessage(ByondMessage, int)} but second argument, which is read timeout time,
     * taken by default as <b>500 ms</b>.
     * @param byondMessage mesasge to send.
     * @return response from BYOND game server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Reason could hide
     * in BYOND game server itself. Typical, it doesn't handle message which was send and, as a result, not provide any response.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException {
        return sendMessage(byondMessage, READ_TIMEOUT);
    }

    /**
     * Send message to BYOND game server with getting response.
     * @param byondMessage message to send.
     * @param readTimeout timeout time to read response.
     * @return response from BYOND game server as {@link io.github.spair.byond.message.response.ByondResponse}
     * or as null, if message response type was {@link io.github.spair.byond.message.response.ResponseType#NONE}.
     * @throws HostUnavailableException signals that requested server unavailable to connect. Offline or some other reason.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual.
     * @throws EmptyResponseException thrown if response was empty, but user wait for something. Reason could hide
     * in BYOND game server itself. Typical, it doesn't handle message which was send and, as result, not provide any response.
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

    /**
     * Method to compare actual and expected {@link io.github.spair.byond.message.response.ResponseType}.
     * @param expected expected response type.
     * @param actual actual response type.
     * @throws UnexpectedResponseTypeException signals that expected and actual response types doesn't equals.
     */
    private void validateResponseType(ResponseType expected, ResponseType actual) throws UnexpectedResponseTypeException {
        if (expected != ResponseType.ANY && expected != actual) {
            throw new UnexpectedResponseTypeException(
                    "Actual response type doesn't equals to what expected. " +
                            "Expected: " + expected + ". Actual: " + actual);
        }
    }
}
