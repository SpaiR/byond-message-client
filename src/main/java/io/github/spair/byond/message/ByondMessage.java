package io.github.spair.byond.message;

import io.github.spair.byond.message.response.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for message, which should be send.
 * Has server address, message itself and expected response type.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ByondMessage {

    private ServerAddress serverAddress;
    private String message;
    private ResponseType expectedResponse = ResponseType.ANY;

    public ByondMessage(ServerAddress serverAddress, String message) {
        this.serverAddress = serverAddress;
        this.message = message;
    }
}
