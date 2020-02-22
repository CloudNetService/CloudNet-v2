package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.CloudNetwork;

/**
 * This event is called when the CloudNetwork is updated.
 */
public class ProxiedCloudNetworkUpdateEvent extends ProxiedCloudEvent {

    private CloudNetwork cloudNetwork;

    public ProxiedCloudNetworkUpdateEvent(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    /**
     * @return the new updated state of the cloud network.
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }
}
