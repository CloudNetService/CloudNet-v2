/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

/**
 * Configuration class for the web server
 */
public class WebServerConfig {

    /**
     * Whether the web server is enabled or not
     */
    private boolean enabled;

    /**
     * The address the web server is bound to
     */
    private String address;

    /**
     * Port that this web server is bound to
     */
    private int port;

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
