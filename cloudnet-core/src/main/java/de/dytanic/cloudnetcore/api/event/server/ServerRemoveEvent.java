/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;

/**
 * Calls if one server is removed and unwhitelisted from a wrapper
 */
public class ServerRemoveEvent extends AsyncEvent<ServerRemoveEvent> {

    private MinecraftServer minecraftServer;

    public ServerRemoveEvent(MinecraftServer minecraftServer) {
        super(new AsyncPosterAdapter<>());
        this.minecraftServer = minecraftServer;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
}
