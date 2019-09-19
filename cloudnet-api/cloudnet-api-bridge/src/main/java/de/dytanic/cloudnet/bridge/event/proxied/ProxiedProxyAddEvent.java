/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;

/**
 * Calls if a proxy server was add into the network
 */
public class ProxiedProxyAddEvent extends ProxiedCloudEvent {

    private ProxyInfo proxyInfo;

    public ProxiedProxyAddEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
