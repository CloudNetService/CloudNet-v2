package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * This event is called when a player logs out of the network by disconnecting from the proxy.
 * Expect the player to have disconnected when handling the event.
 */
public class ProxiedPlayerLogoutEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public ProxiedPlayerLogoutEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    /**
     * @return the player that left the network.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
