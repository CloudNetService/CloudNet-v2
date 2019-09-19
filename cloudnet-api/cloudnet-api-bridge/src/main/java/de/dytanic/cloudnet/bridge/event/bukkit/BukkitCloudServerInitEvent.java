/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.bridge.CloudServer;
import org.bukkit.event.HandlerList;

/**
 * Calls before the first init update of the serverInfo for a online state
 */
public class BukkitCloudServerInitEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private CloudServer cloudServer;

    public BukkitCloudServerInitEvent(CloudServer cloudServer) {
        this.cloudServer = cloudServer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public CloudServer getCloudServer() {
        return cloudServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
