package eu.cloudnetservice.cloudnet.v2.web.server.util;

/**
 * Configuration class for the web server
 */
public class WebServerConfig {

    /**
     * Whether the web server is enabled or not
     */
    private final boolean enabled;

    /**
     * The address the web server is bound to
     */
    private final String address;

    /**
     * Port that this web server is bound to
     */
    private final int port;

    public WebServerConfig(boolean enabled, String address, int port) {
        this.enabled = enabled;
        this.address = address;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
