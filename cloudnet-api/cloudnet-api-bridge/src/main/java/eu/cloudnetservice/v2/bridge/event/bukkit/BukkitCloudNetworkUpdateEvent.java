package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.api.handlers.NetworkHandler;
import eu.cloudnetservice.v2.lib.CloudNetwork;
import org.bukkit.event.HandlerList;

/**
 * This event is called when the CloudNetwork is updated.
 * <p>
 * {@link NetworkHandler#onCloudNetworkUpdate(CloudNetwork)}
 * </p>
 */
public class BukkitCloudNetworkUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final CloudNetwork cloudNetwork;

    /**
     * Constructs a new event for notifying other plugins that the CloudNetwork has been updated.
     *
     * @param cloudNetwork the new state of the cloud network.
     */
    public BukkitCloudNetworkUpdateEvent(CloudNetwork cloudNetwork) {
        super();
        this.cloudNetwork = cloudNetwork;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the new updated state of the cloud network.
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
