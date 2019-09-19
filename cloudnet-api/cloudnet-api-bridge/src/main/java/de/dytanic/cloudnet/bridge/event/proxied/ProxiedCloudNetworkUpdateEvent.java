/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.CloudNetwork;

/**
 * Called if the cloudnetwork objective was updated
 */
public class ProxiedCloudNetworkUpdateEvent extends ProxiedCloudEvent {

    private CloudNetwork cloudNetwork;

    public ProxiedCloudNetworkUpdateEvent(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }
}
