package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import org.bukkit.event.HandlerList;

/**
 * This event is called whenever a {@link CloudPlayer} instance is updated on the network.
 * This event does not contain either the prior state nor the nature of or the cause for the update.
 */
public class BukkitPlayerUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final CloudPlayer cloudPlayer;

    public BukkitPlayerUpdateEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the newly updated player instance.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
