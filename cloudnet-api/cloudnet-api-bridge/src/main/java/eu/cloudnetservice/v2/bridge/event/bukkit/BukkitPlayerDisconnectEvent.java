package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a player leaves the network.
 */
public class BukkitPlayerDisconnectEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudPlayer cloudPlayer;

    public BukkitPlayerDisconnectEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the player that just left the network.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
