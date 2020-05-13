package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;

/**
 * This event is called whenever a proxy has been removed from the CloudNet network.
 * The proxy is <b>not</b> connected to the network anymore.
 */
public class ProxiedProxyRemoveEvent extends ProxiedCloudEvent {

    private final ProxyInfo proxyInfo;

    public ProxiedProxyRemoveEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    /**
     * The proxy information about the proxy that has been removed from the network.
     *
     * @return the proxy information about the removed proxy.
     */
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
