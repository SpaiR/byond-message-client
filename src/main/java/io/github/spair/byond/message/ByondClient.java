package io.github.spair.byond.message;

import io.github.spair.byond.message.exception.HostUnavailableException;
import io.github.spair.byond.message.exception.UnexpectedResponseException;
import lombok.val;

import java.nio.charset.Charset;

/**
 * <p>Class to send string messages from Java app to BYOND server.
 * <p>Simple usage example:
 * <pre>{@code
 *      ByondMessage messageToSend = new ByondMessage(new ServerAddress("bagil.game.tgstation13.org", 2337), "?ping");
 *      ByondResponse response = new ByondClient().sendMessage(messageToSend);
 * }</pre>
 * <p>ServerAddress could be omitted like that: {@code new ByondMessage("bagil.game.tgstation13.org", 2337, "ping")}
 */
@SuppressWarnings("WeakerAccess")
public final class ByondClient {

    /** <b>cp1251</b> {@link Charset}, used by BYOND. */
    public static final Charset BYOND_CHARSET = Charset.forName("cp1251");

    private ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
    private ByondResponseConverter responseConverter = new ByondResponseConverter();

    /**
     * Sends message to BYOND server without waiting for response.
     *
     * @param byondMessage message to send
     * @throws HostUnavailableException signals that requested server unavailable to connect
     */
    public void sendCommand(final ByondMessage byondMessage) throws HostUnavailableException {
        byondMessage.setExpectedResponse(ResponseType.NONE);
        sendMessage(byondMessage, 0);
    }

    /**
     * <p>Sends message to BYOND server with wait and returning of response.
     * <p>Method is blocking. Timeout time created from connection lag and sending/reading response time.
     * Unlike {@link ByondClient#sendMessage(ByondMessage, int)} additional wait from timeout won't be added,
     * but method is still guarantee, that whole data will be got.
     *
     * @param byondMessage message object to send
     * @return Response from BYOND server as {@link ByondResponse} or null,
     * if expected response is {@link ResponseType#NONE}
     * @throws HostUnavailableException    signals that requested server unavailable to connect
     * @throws UnexpectedResponseException if somehow response has unexpected behavior
     */
    public ByondResponse sendMessage(final ByondMessage byondMessage) throws HostUnavailableException, UnexpectedResponseException {
        return sendMessage(byondMessage, 0);
    }

    /**
     * <p>Sends message to BYOND server with wait and returning of response. Custom timeout wait could be insert.
     * <p>Method is blocking. Timeout time created from connection lag, sending time and reading timeout.
     * If timeout expired before response will be fully read, response data will be incomplete, but still exist.
     * Zero and less timeout value means,
     * that actual read will be performed like in {@link ByondClient#sendMessage(ByondMessage)},
     * without any custom timeout at all.
     * <p>Method is <b>not recommended</b> to be used, due to extra wait time from timeout,
     * and possibility of shredding data if timeout is too low.
     *
     * @param byondMessage message object to send
     * @param readTimeout  timeout time to read response
     * @return Response from BYOND server as {@link ByondResponse} or null,
     * if expected response is {@link ResponseType#NONE}
     * @throws HostUnavailableException    signals that requested server unavailable to connect.
     * @throws UnexpectedResponseException if somehow response has unexpected behavior
     */
    public ByondResponse sendMessage(final ByondMessage byondMessage, final int readTimeout) throws HostUnavailableException, UnexpectedResponseException {
        val withResponse = (byondMessage.getExpectedResponse() != ResponseType.NONE);
        val comm = new SocketCommunicator(byondMessage.getServerAddress(), readTimeout, withResponse);

        val messageTopic = byondMessage.getMessageAsTopic();
        val rawServerResponse = comm.communicate(byteArrayConverter.convertIntoBytes(messageTopic));

        if (withResponse) {
            val byondResponse = responseConverter.convertIntoResponse(rawServerResponse);
            validateResponseType(byondMessage.getExpectedResponse(), byondResponse.getResponseType());
            return byondResponse;
        } else {
            return null;
        }
    }

    private void validateResponseType(final ResponseType expected, final ResponseType actual) {
        if (expected != ResponseType.ANY && expected != actual) {
            throw new UnexpectedResponseException(
                    String.format("Actual response type doesn't equals to expected. Expected: %s. Actual: %s", expected, actual)
            );
        }
    }
}
