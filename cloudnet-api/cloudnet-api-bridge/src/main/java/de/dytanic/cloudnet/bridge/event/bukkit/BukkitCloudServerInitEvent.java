package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.bridge.CloudServer;
import org.bukkit.event.HandlerList;

/**
 * This event is called right before the first (initial) update of the current cloud server is being done.
 * This event can be used to determine, when the server is ready to accept connections.
 */
public class BukkitCloudServerInitEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudServer cloudServer;

    public BukkitCloudServerInitEvent(CloudServer cloudServer) {
        super();
        this.cloudServer = cloudServer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    /**
     * @return the server that has been initialized.
     */
    public CloudServer getCloudServer() {
        return cloudServer;
    }
}
