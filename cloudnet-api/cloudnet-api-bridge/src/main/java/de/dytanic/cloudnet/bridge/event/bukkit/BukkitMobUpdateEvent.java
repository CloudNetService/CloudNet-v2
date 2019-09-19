/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.event.HandlerList;

/**
 * Updates a ServerMob
 */
public class BukkitMobUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ServerMob serverMob;

    public BukkitMobUpdateEvent(ServerMob serverMob) {
        this.serverMob = serverMob;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ServerMob getServerMob() {
        return serverMob;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
