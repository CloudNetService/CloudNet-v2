package eu.cloudnetservice.v2.bridge.event.proxied;

import java.util.UUID;

/**
 * This event is called whenever a player logs out from the cloud network.
 * This event may be called for players which are <b>not</b> registered on CloudNet.
 * When handling this event, the player may already be disconnected.
 */
public class ProxiedPlayerLogoutUniqueEvent extends ProxiedCloudEvent {

    private UUID uniqueId;

    public ProxiedPlayerLogoutUniqueEvent(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the unique ID of the player that just logged out.
     */
    public UUID getUniqueId() {
        return uniqueId;
    }
}