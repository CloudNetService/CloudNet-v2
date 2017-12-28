/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.bridge.CloudServer;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls before the first init update of the serverInfo for a online state
 */
@AllArgsConstructor
public class BukkitCloudServerInitEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudServer cloudServer;

    public CloudServer getCloudServer()
    {
        return cloudServer;
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