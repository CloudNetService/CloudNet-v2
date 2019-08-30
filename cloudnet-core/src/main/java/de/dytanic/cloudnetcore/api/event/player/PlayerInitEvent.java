/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PlayerInitEvent extends Event {

    private CloudPlayer cloudPlayer;

    public PlayerInitEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}