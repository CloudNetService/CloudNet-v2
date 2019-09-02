/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayoutConfig;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 19.08.2017.
 */
public class BukkitUpdateSignLayoutsEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private SignLayoutConfig signLayoutConfig;

    public BukkitUpdateSignLayoutsEvent(SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
