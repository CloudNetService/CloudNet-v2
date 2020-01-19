package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * This event is called whenever a {@link CloudPlayer} instance is updated on the network.
 * This event does not contain either the prior state nor the nature of or the cause for the update.
 */
public class ProxiedPlayerUpdateEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public ProxiedPlayerUpdateEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    /**
     * @return the newly updated player instance.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
