package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.client.exceptions.converter.UnknownResponseException;
import io.github.spair.byond.message.ByondResponse;
import io.github.spair.byond.message.ResponseType;

import java.nio.ByteBuffer;

/**
 * <p>Class to send string messages from Java program to BYOND server.
 *
 * <p>Simple usage example:
 * <pre><code>
 * ByondMessage messageToSend = new ByondMessage(new ServerAddress("bagil.game.tgstation13.org", 2337), "?ping");
 *
 * ByondClient client = new ByondClient();
 * ByondResponse response = client.sendMessage(messageToSend);
 * </code></pre>
 *
 * This, with the same result, could be rewritten in the next way:
 * <pre><code>
 * ByondMessage messageToSend = new ByondMessage("bagil.game.tgstation13.org", 2337, "ping");
 * ByondResponse response = ByondClient.create().sendMessage(messageToSend);
 * </code></pre>
 */
public class ByondClient {

    /**
     * Static method to create {@link io.github.spair.byond.message.client.ByondClient} instance
     * without calling of constructor.
     *
     * @return {@link io.github.spair.byond.message.client.ByondClient} instance.
     */
    public static ByondClient create() {
        return new ByondClient();
    }

    /**
     * Sends message to BYOND server without waiting for response.
     *
     * @param byondMessage message object to send.
     *
     * @throws HostUnavailableException signals that requested server unavailable to connect.
     */
    public void sendCommand(ByondMessage byondMessage) throws HostUnavailableException {
        byondMessage.setExpectedResponse(ResponseType.NONE);
        sendMessage(byondMessage, 0);
    }

    /**
     * <p>Sends message to BYOND server with wait and returning of response.
     *
     * <p>Method is blocking. Waiting time created from connection lag and sending/reading response time.
     * Unlike {@link ByondClient#sendMessage(ByondMessage, int)} additional wait from timeout won't be added,
     * but method still guarantee, that whole data will be got.
     *
     * @param byondMessage message object to send.
     *
     * @return Response from BYOND server as {@link ByondResponse}.
     *         If message expecting response type is {@link ResponseType#NONE},
     *         then response instance will has 'null' in 'responseData' field
     *         and {@link ResponseType#NONE} in 'responseType' field.
     *
     * @throws HostUnavailableException signals that requested server unavailable to connect.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual got.
     * @throws EmptyResponseException thrown if response was empty, but user waiting for something.
     *                                Cause could be in BYOND server itself.
     *                                Typically, it doesn't handle message which was send and, as a result, not provide any response.
     * @throws UnknownResponseException signals that response was caught, but it encoded into unknown format.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException, UnknownResponseException {
        return sendMessage(byondMessage, 0);
    }

    /**
     * <p>Sends message to BYOND server with wait and of returning response. Custom timeout wait could be insert.
     *
     * <p>Method is blocking. Waiting time created from connection lag, sending time and reading timeout.
     * If timeout expired before response fully read data will be incomplete, but still exist.
     * Zero and less timeout value means, that actual read will be performed like in {@link ByondClient#sendMessage(ByondMessage)},
     * without any custom timeout at all.
     *
     * <p>Method is <b>not recommended</b> to be used, due to extra wait time from timeout,
     * and possibility of shredding data if timeout is too low.
     *
     * @param byondMessage message object to send.
     * @param readTimeout timeout time to read response.
     *
     * @return Response from BYOND server as {@link ByondResponse}.
     *         If message expecting response type is {@link ResponseType#NONE},
     *         then response instance will has 'null' in 'responseData' field
     *         and {@link ResponseType#NONE} in 'responseType' field.
     *
     * @throws HostUnavailableException signals that requested server unavailable to connect.
     * @throws UnexpectedResponseTypeException thrown if expected response doesn't equals to actual got.
     * @throws EmptyResponseException thrown if response was empty, but user waiting for something.
     *                                Cause could be in BYOND server itself.
     *                                Typically, it doesn't handle message which was send and, as a result, not provide any response.
     * @throws UnknownResponseException signals that response was caught, but it encoded into unknown format.
     */
    public ByondResponse sendMessage(ByondMessage byondMessage, int readTimeout)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException, UnknownResponseException
    {
        boolean closeCommAfterSend = (byondMessage.getExpectedResponse() == ResponseType.NONE);
        SocketCommunicator comm = new SocketCommunicator(byondMessage.getServerAddress(), readTimeout, closeCommAfterSend);

        String messageTopic = byondMessage.getMessageAsTopic();
        ByteBuffer rawServerResponse = comm.communicate(ByteArrayConverter.convertIntoBytes(messageTopic));

        if (!closeCommAfterSend) {
            ByondResponse byondResponse = ByondResponseConverter.convertIntoResponse(rawServerResponse);
            validateResponseType(byondMessage.getExpectedResponse(), byondResponse.getResponseType());
            return byondResponse;
        } else {
            return new ByondResponse(null, ResponseType.NONE);
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
