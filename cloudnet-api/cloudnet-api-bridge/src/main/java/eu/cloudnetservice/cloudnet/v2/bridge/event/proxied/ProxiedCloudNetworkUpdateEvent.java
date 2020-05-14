package eu.cloudnetservice.cloudnet.v2.bridge.event.proxied;

import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;

/**
 * This event is called when the CloudNetwork is updated.
 */
public class ProxiedCloudNetworkUpdateEvent extends ProxiedCloudEvent {

    private final CloudNetwork cloudNetwork;

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
