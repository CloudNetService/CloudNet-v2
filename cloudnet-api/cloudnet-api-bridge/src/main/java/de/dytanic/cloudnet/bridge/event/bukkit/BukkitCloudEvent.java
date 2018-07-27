/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import org.bukkit.event.Event;

/**
 * This Class defind the Bukkit CloudNet Event API
 */
public abstract class BukkitCloudEvent extends Event {

    /**
     * Returns the CloudAPI instance
     *
     * @return
     */
    public CloudAPI getCloud()
    {
        return CloudAPI.getInstance();
    }

    /**
     * Returns the CloudServer instance
     */
    public CloudServer getCloudServer()
    {
        return CloudServer.getInstance();
    }

    /**
     * Execute the CloudServer update(); method
     */
    public void update()
    {
        CloudServer.getInstance().update();
    }

}