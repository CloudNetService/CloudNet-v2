/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.bukkit.event.HandlerList;

/**
 * Created by Tareko on 19.08.2017.
 */
@AllArgsConstructor
public class BukkitSignListUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private Map<UUID, Sign> signList;

    public Map<UUID, Sign> getSignList()
    {
        return signList;
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