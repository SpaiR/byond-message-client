package io.github.spair.byond.message;

import java.util.Objects;

/**
 * Wrapper for server address.
 * Server name can be represented as DNS (game.server.com) or IP (1.2.3.4) value.
 */
@SuppressWarnings("unused")
public class ServerAddress {

    private String name;
    private int port;

    public ServerAddress() {
    }

    public ServerAddress(final String name, final int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ServerAddress{"
                + "name='" + name + '\''
                + ", port=" + port
                + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerAddress that = (ServerAddress) o;
        return port == that.port && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, port);
    }
}
