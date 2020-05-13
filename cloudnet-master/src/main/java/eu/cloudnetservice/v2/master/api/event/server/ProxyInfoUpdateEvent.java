package eu.cloudnetservice.v2.master.api.event.server;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

/**
 * Calls if a proxy updated it ProxyInfo
 */
public class ProxyInfoUpdateEvent extends AsyncEvent<ProxyInfoUpdateEvent> {

    private final ProxyInfo proxyInfo;

    private final ProxyServer proxyServer;

    public ProxyInfoUpdateEvent(ProxyServer proxyServer, ProxyInfo proxyInfo) {
        super(new AsyncPosterAdapter<>());
        this.proxyServer = proxyServer;
        this.proxyInfo = proxyInfo;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }
}
