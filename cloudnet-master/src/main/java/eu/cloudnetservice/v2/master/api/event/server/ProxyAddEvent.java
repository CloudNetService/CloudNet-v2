package eu.cloudnetservice.v2.master.api.event.server;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

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
