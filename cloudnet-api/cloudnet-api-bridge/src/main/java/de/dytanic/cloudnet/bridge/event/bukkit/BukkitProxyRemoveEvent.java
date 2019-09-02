/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import org.bukkit.event.HandlerList;

/**
 * Calls if a proxy server is removed from the network
 */
public class BukkitProxyRemoveEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ProxyInfo serverInfo;

    public BukkitProxyRemoveEvent(ProxyInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ProxyInfo getProxyInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
