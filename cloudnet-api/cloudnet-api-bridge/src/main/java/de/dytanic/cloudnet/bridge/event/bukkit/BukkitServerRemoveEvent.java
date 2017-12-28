/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls if a server was removed from the network
 */
@AllArgsConstructor
public class BukkitServerRemoveEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ServerInfo serverInfo;

    public ServerInfo getServerInfo()
    {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
}
