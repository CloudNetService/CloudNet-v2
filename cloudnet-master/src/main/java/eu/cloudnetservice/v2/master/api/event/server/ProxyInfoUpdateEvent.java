package eu.cloudnetservice.v2.master.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

/**
 * Calls if a proxy updated it ProxyInfo
 */
public class ProxyInfoUpdateEvent extends AsyncEvent<ProxyInfoUpdateEvent> {

    private ProxyInfo proxyInfo;

    private ProxyServer proxyServer;

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