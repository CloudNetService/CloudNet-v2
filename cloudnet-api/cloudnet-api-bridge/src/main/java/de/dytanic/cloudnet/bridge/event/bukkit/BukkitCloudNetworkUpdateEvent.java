/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.CloudNetwork;
import org.bukkit.event.HandlerList;

/**
 * Calls if the cloudnetwork objective was updated
 */
public class BukkitCloudNetworkUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudNetwork cloudNetwork;

    public BukkitCloudNetworkUpdateEvent(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
