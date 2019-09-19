/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Calls if a Player was logged into the network
 */
public class LoginEvent extends AsyncEvent<LoginEvent> {

    private CloudPlayer cloudPlayer;

    public LoginEvent(CloudPlayer cloudPlayer) {
        super(new AsyncPosterAdapter<>());
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
