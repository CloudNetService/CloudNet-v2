package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * This event is called whenever a player successfully logs on to the cloud network.
 */
public class ProxiedPlayerLoginEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public ProxiedPlayerLoginEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    /**
     * @return the player that has just logged on to the network.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
