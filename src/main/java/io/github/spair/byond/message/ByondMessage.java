package io.github.spair.byond.message;

import io.github.spair.byond.message.response.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
