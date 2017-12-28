/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import lombok.Getter;

/**
 * Calls if a player is updated on cloudnet
 */
@Getter
public class UpdatePlayerEvent extends AsyncEvent<UpdatePlayerEvent>{

    private OfflinePlayer offlinePlayer;

    public UpdatePlayerEvent(OfflinePlayer offlinePlayer)
    {
        super(new AsyncPosterAdapter<>());
        this.offlinePlayer = offlinePlayer;
    }
}