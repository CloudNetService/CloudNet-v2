package eu.cloudnetservice.v2.master.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;

/**
 * Calls if a proxy server was removed from the network and whitelist
 */
public class ProxyRemoveEvent extends AsyncEvent<ProxyRemoveEvent> {

    private ProxyServer proxyServer;

    public ProxyRemoveEvent(ProxyServer proxyServer) {
        super(new AsyncPosterAdapter<>());
        this.proxyServer = proxyServer;
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }
}
