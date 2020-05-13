package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a server has been removed from the CloudNet network.
 * When receiving this event, the server has already been stopped.
 */
public class BukkitServerRemoveEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ServerInfo serverInfo;

    public BukkitServerRemoveEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The server information object of the removed server.
     *
     * @return the information of the removed server.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
