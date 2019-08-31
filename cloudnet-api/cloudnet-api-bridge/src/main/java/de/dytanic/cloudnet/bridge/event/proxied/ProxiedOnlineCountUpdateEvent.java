/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Tareko on 11.10.2017.
 */
public class ProxiedOnlineCountUpdateEvent extends Event {

    private int onlineCount;

    public ProxiedOnlineCountUpdateEvent(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public int getOnlineCount() {
        return onlineCount;
    }
}