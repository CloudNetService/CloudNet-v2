/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 18.08.2017.
 */
@AllArgsConstructor
public class BukkitPlayerDisconnectEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudPlayer cloudPlayer;

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}