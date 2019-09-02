/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import org.bukkit.event.HandlerList;

/**
 * Calls if a player logins to the network successfully
 */
public class BukkitPlayerLoginNetworkEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudPlayer cloudPlayer;

    public BukkitPlayerLoginNetworkEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
