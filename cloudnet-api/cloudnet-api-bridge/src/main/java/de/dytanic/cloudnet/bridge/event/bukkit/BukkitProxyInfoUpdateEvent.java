/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls if the proxyInfo from one proxy was updated
 */
@AllArgsConstructor
public class BukkitProxyInfoUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private ProxyInfo serverInfo;

    public ProxyInfo getProxyInfo()
    {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

}