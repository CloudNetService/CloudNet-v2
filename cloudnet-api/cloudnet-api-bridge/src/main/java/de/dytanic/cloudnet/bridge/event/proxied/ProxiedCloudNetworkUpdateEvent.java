/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.CloudNetwork;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

/**
 * Called if the cloudnetwork objective was updated
 */
@AllArgsConstructor
public class ProxiedCloudNetworkUpdateEvent extends ProxiedCloudEvent {

    private CloudNetwork cloudNetwork;

    public CloudNetwork getCloudNetwork()
    {
        return cloudNetwork;
    }
}