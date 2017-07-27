package io.github.spair.byond.message.client;

import io.github.spair.byond.message.ByondMessage;
import io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException;
import io.github.spair.byond.message.client.exceptions.communicator.HostUnavailableException;
import io.github.spair.byond.message.client.exceptions.converter.EmptyResponseException;
import io.github.spair.byond.message.response.ByondResponse;
import io.github.spair.byond.message.response.ResponseType;

import java.nio.ByteBuffer;

public class ByondClient {

    private final MessageConverter messageConverter = new MessageConverter();
    private final int READ_TIMEOUT = 500;

    public static ByondClient create() {
        return new ByondClient();
    }

    public void sendCommand(ByondMessage byondMessage) throws HostUnavailableException {
        byondMessage.setExpectedResponse(ResponseType.NONE);
        sendMessage(byondMessage, READ_TIMEOUT);
    }

    public ByondResponse sendMessage(ByondMessage byondMessage)
            throws HostUnavailableException, UnexpectedResponseTypeException, EmptyResponseException {
        return sendMessage(byondMessage, READ_TIMEOUT);
    }

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
