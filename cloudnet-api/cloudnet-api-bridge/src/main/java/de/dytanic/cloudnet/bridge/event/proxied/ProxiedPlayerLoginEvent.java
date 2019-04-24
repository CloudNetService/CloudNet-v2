/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;

/**
 * Calls if a player login into the network successfully
 */
@AllArgsConstructor
public class ProxiedPlayerLoginEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}