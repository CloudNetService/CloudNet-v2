package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a player successfully logs on to the cloud network.
 */
public class BukkitPlayerLoginNetworkEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudPlayer cloudPlayer;

    public BukkitPlayerLoginNetworkEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the player that has just logged on to the network.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
