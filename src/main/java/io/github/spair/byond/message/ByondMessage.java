package io.github.spair.byond.message;

import io.github.spair.byond.message.response.ResponseType;

/**
 * <p>Container for message, which will be sent to BYOND server.</p>
 *
 * <p>Has server address as {@link io.github.spair.byond.message.ServerAddress}
 * (used by {@link io.github.spair.byond.message.client.ByondClient} to know, where message should be send),
 * message as {@link java.lang.String}
 * and type of expected response as {@link io.github.spair.byond.message.response.ResponseType}.
 *
 * <p>Message must contain question mark at start, but it could be omitted,
 * because it will be added on send process automatically if missing. So "ping" and "?ping" will do the same.
 * Also, message itself is like a parameters for HTTP request, so multiple messages in one send is possible.
 * To do it all params should be divided with "{@literal ;}" or "{@literal &}".
 * For example: "{@code ping&data=123&status}".</p>
 *
 * <p>Expected response type checked on response validation process. If real response doesn't match,
 * exception {@link io.github.spair.byond.message.client.exceptions.UnexpectedResponseTypeException} will be thrown.
 * Default expected response is {@link io.github.spair.byond.message.response.ResponseType#ANY}.</p>
 */
@SuppressWarnings("unused")
public class ByondMessage {

    private ServerAddress serverAddress;
    private String message;
    private ResponseType expectedResponse = ResponseType.ANY;

    public ByondMessage() {}

    public ByondMessage(ServerAddress serverAddress, String message) {
        this.serverAddress = serverAddress;
        this.message = message;
    }

    public ByondMessage(ServerAddress serverAddress, String message, ResponseType expectedResponse) {
        this.serverAddress = serverAddress;
        this.message = message;
        this.expectedResponse = expectedResponse;
    }

    public ByondMessage(String serverName, int serverPort, String message) {
        this.serverAddress = new ServerAddress(serverName, serverPort);
        this.message = message;
    }

    public ByondMessage(String serverName, int serverPort, String message, ResponseType expectedResponse) {
        this.serverAddress = new ServerAddress(serverName, serverPort);
        this.message = message;
        this.expectedResponse = expectedResponse;
    }

    public ServerAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseType getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(ResponseType expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByondMessage message1 = (ByondMessage) o;

        return (serverAddress != null ? serverAddress.equals(message1.serverAddress) : message1.serverAddress == null)
                && (message != null ? message.equals(message1.message) : message1.message == null)
                && expectedResponse == message1.expectedResponse;
    }

    @Override
    public int hashCode() {
        int result = serverAddress != null ? serverAddress.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (expectedResponse != null ? expectedResponse.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ByondMessage{" +
                "serverAddress=" + serverAddress +
                ", message='" + message + '\'' +
                ", expectedResponse=" + expectedResponse +
                '}';
    }
}
