package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a proxy has been removed from the CloudNet network.
 * The proxy is <b>not</b> connected to the network anymore.
 */
public class BukkitProxyRemoveEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyInfo serverInfo;

    public BukkitProxyRemoveEvent(ProxyInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The proxy information about the proxy that has been removed from the network.
     *
     * @return the proxy information about the removed proxy.
     */
    public ProxyInfo getProxyInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
