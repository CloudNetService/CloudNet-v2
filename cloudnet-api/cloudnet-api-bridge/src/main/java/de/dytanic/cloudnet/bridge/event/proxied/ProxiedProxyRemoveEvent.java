/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;

/**
 * Calls if a proxy server is removed from the network
 */
public class ProxiedProxyRemoveEvent extends ProxiedCloudEvent {

    private ProxyInfo proxyInfo;

    public ProxiedProxyRemoveEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
