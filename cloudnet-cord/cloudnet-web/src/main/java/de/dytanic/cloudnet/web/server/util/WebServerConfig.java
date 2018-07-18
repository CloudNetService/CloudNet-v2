/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Configuration class for the web server
 */
@Getter
@AllArgsConstructor
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

}
