package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;

/**
 * This event is called whenever the proxy information for a proxy on the
 * network has been updated.
 */
public class ProxiedProxyInfoUpdateEvent extends ProxiedCloudEvent {

    private ProxyInfo proxyInfo;

    public ProxiedProxyInfoUpdateEvent(ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    /**
     * The updated proxy information object.
     *
     * @return the updated proxy information.
     */
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
