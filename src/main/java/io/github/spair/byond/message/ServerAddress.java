package io.github.spair.byond.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper for server address with server IP/DNS and port.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerAddress {

    private String name;
    private int port;
}
