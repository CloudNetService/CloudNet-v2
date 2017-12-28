/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Updates a ServerMob
 */
@AllArgsConstructor
public class BukkitMobUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ServerMob serverMob;

    public ServerMob getServerMob()
    {
        return serverMob;
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