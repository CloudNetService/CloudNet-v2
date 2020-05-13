package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever server information is updated on the network.
 */
public class BukkitServerInfoUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerInfo serverInfo;

    public BukkitServerInfoUpdateEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the updated server information.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
