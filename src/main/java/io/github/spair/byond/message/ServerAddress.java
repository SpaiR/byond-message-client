package io.github.spair.byond.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper for server address.
 * Server name can be represented as DNS (game.server.com) or IP (1.2.3.4) value.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerAddress {
    private String name;
    private int port;
}
