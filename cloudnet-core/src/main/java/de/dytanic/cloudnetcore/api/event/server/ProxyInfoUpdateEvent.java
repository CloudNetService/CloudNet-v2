/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import lombok.Getter;

/**
 * Calls if a proxy updated it ProxyInfo
 */
@Getter
public class ProxyInfoUpdateEvent extends AsyncEvent<ProxyInfoUpdateEvent> {

    private ProxyInfo proxyInfo;

    private ProxyServer proxyServer;

    public ProxyInfoUpdateEvent(ProxyServer proxyServer, ProxyInfo proxyInfo)
    {
        super(new AsyncPosterAdapter<>());
        this.proxyServer = proxyServer;
        this.proxyInfo = proxyInfo;
    }
}