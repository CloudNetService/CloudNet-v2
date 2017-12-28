/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calls if a subChannelMessage was send
 */
@AllArgsConstructor
public class BukkitSubChannelMessageEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private String channel;

    private String message;

    private Document document;

    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    public String getMessage()
    {
        return message;
    }

    public Document getDocument()
    {
        return document;
    }

    public String getChannel()
    {
        return channel;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }
}