/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;

import java.util.UUID;

/**
 * Calls if a player
 */
public class LogoutEventUnique extends AsyncEvent<LogoutEventUnique> {

    private UUID uniqueId;

    public LogoutEventUnique(UUID uniqueId) {
        super(new AsyncPosterAdapter<>());
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
