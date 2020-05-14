package eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit;

import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a server has been added to the CloudNet network.
 * When receiving this event, the server might not be done with starting up.
 */
public class BukkitServerAddEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerInfo serverInfo;

    public BukkitServerAddEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * The server information object about the server that has been added to the network.
     *
     * @return the server information of the added server.
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
