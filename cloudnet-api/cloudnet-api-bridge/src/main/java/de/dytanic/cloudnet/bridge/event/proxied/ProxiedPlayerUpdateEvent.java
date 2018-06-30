/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;

/**
 * Calls if a online player was updated on network
 */
@AllArgsConstructor
public class ProxiedPlayerUpdateEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public CloudPlayer getCloudPlayer()
    {
        return cloudPlayer;
    }
}