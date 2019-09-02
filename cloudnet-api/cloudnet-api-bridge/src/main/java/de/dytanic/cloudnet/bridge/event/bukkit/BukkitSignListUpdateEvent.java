/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import org.bukkit.event.HandlerList;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 19.08.2017.
 */
public class BukkitSignListUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private Map<UUID, Sign> signList;

    public BukkitSignListUpdateEvent(Map<UUID, Sign> signList) {
        this.signList = signList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Map<UUID, Sign> getSignList() {
        return signList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
