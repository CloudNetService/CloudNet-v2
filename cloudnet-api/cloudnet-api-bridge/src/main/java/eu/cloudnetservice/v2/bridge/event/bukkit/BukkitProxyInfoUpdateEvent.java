package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever the proxy information for a proxy on the
 * network has been updated.
 */
public class BukkitProxyInfoUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyInfo serverInfo;

    public BukkitProxyInfoUpdateEvent(ProxyInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The updated proxy information object.
     *
     * @return the updated proxy information.
     */
    public ProxyInfo getProxyInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
