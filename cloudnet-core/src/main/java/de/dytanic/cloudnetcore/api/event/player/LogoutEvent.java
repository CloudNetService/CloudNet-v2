/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Calls if a Player loguts from the proxy
 */
public class LogoutEvent extends Event {

    private CloudPlayer playerWhereAmI;

    public LogoutEvent(CloudPlayer playerWhereAmI) {
        this.playerWhereAmI = playerWhereAmI;
    }

    public CloudPlayer getPlayerWhereAmI() {
        return playerWhereAmI;
    }
}