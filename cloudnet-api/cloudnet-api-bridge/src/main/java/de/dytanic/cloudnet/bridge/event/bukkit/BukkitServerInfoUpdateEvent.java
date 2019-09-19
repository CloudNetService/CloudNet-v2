/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import org.bukkit.event.HandlerList;

/**
 * Calls if a server was updated
 */
public class BukkitServerInfoUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ServerInfo serverInfo;

    public BukkitServerInfoUpdateEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
