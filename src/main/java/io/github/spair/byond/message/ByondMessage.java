package io.github.spair.byond.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Container for message, which will be sent to BYOND server.
 * <p>Message must contain question mark in the beginning of string, but it could be omitted,
 * because it will be added on send process automatically if missing. So "ping" and "?ping" will have the same result.
 * Also, message itself is like a parameters for HTTP request, so multiple messages in one send is possible.
 * To do it all params should be divided with ";" or "{@literal &}" marks.
 * For example: "{@code ping&data=123&status}".
 * <p>Expected response type checked on response validation process. If real response doesn't match,
 * exception {@link io.github.spair.byond.message.exception.UnexpectedResponseException} will be thrown.
 * Default expected response is {@link ResponseType#ANY}.
 */
@Data
@NoArgsConstructor
@SuppressWarnings("WeakerAccess")
public class ByondMessage {

    private ServerAddress serverAddress;
    private String message;
    private ResponseType expectedResponse = ResponseType.ANY;

    public ByondMessage(final ServerAddress serverAddress, final String message) {
        this.serverAddress = serverAddress;
        this.message = message;
    }

    public ByondMessage(final ServerAddress serverAddress, final String message, final ResponseType expectedResponse) {
        this.serverAddress = serverAddress;
        this.message = message;
        this.expectedResponse = expectedResponse;
    }

    public ByondMessage(final String serverName, final int serverPort, final String message) {
        this.serverAddress = new ServerAddress(serverName, serverPort);
        this.message = message;
    }

    public ByondMessage(final String serverName, final int serverPort, final String message, final ResponseType expectedResponse) {
        this.serverAddress = new ServerAddress(serverName, serverPort);
        this.message = message;
        this.expectedResponse = expectedResponse;
    }

    /**
     * Checks if message has question mark in the beginning, and, if it isn't,
     * adds it and return new string, so message in 'this' instance doesn't touched.
     * Otherwise already exist message will be returned.
     *
     * @return Message string with question mark in the beginning.
     */
    public String getMessageAsTopic() {
        if (message != null && !message.startsWith("?")) {
            return "?" + message;
        } else {
            return message;
        }
    }
}
