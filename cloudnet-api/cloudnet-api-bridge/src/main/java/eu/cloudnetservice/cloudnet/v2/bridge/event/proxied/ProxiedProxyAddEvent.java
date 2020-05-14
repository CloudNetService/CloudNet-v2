package eu.cloudnetservice.cloudnet.v2.bridge.event.proxied;

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;

/**
 * This event is called when a new proxy is added to the CloudNet network.
 * The proxy may not be done with its initialization when receiving this event.
 */
public class ProxiedProxyAddEvent extends ProxiedCloudEvent {

    private final ProxyInfo proxyInfo;

    public ProxiedProxyAddEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    /**
     * The proxy information object of the proxy that has been added to the network.
     *
     * @return the information about the newly added proxy.
     */
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
