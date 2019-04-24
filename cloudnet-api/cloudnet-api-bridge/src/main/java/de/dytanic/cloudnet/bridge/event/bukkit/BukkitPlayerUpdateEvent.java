/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.event.HandlerList;

/**
 * Calls if a player was updated on network
 */
@AllArgsConstructor
public class BukkitPlayerUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    private CloudPlayer cloudPlayer;

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}