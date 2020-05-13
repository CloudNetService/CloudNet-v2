package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a new proxy is added to the CloudNet network.
 * The proxy may not be done with its initialization when receiving this event.
 */
public class BukkitProxyAddEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ProxyInfo serverInfo;

    public BukkitProxyAddEvent(ProxyInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The proxy information object of the proxy that has been added to the network.
     *
     * @return the information about the newly added proxy.
     */
    public ProxyInfo getProxyInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
