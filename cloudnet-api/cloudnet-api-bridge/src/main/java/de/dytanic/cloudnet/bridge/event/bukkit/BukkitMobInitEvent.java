/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import lombok.AllArgsConstructor;
import org.bukkit.event.HandlerList;

/**
 * Calls if a server mob is created
 */
@AllArgsConstructor
public class BukkitMobInitEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private MobSelector.MobImpl mob;

    public MobSelector.MobImpl getMob()
    {
        return mob;
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