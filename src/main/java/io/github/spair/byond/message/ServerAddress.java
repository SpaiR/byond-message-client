package io.github.spair.byond.message;

/**
 * Wrapper for server address.
 * <br>
 * Contains server name, which is {@link java.lang.String} and server port as integer value.
 * Server name can be represented as DNS (game.server.com) or IP (123.123.123.123).
 */
@SuppressWarnings("unused")
public class ServerAddress {

    private String name;
    private int port;

    public ServerAddress() {}

    public ServerAddress(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerAddress that = (ServerAddress) o;

        return port == that.port && (name != null ? name.equals(that.name) : that.name == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "ServerAddress{" +
                "name='" + name + '\'' +
                ", port=" + port +
                '}';
    }
}
