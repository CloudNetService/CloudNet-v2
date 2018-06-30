/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import lombok.AllArgsConstructor;

/**
 * Calls if a proxy server updates the proxyInfo
 */
@AllArgsConstructor
public class ProxiedProxyInfoUpdateEvent extends ProxiedCloudEvent {

    private ProxyInfo proxyInfo;

    public ProxyInfo getProxyInfo()
    {
        return proxyInfo;
    }
}