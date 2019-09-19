/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Calls if a player logouts into
 */
public class ProxiedPlayerLogoutEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    public ProxiedPlayerLogoutEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
