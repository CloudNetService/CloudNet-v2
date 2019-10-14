/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Calls if a proxy was whitelisted to from a wrapper and the proxy start successfully
 */
public class ProxyAddEvent extends AsyncEvent<ProxyAddEvent> {

    private ProxyServer proxyServer;

    public ProxyAddEvent(ProxyServer proxyServer) {
        super(new AsyncPosterAdapter<>());
        this.proxyServer = proxyServer;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }
}
