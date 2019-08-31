/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.server;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.components.CloudServer;

/**
 * Created by Tareko on 25.10.2017.
 */
public class CloudServerRemoveEvent extends Event {

    private CloudServer cloudServer;

    public CloudServerRemoveEvent(CloudServer cloudServer) {
        this.cloudServer = cloudServer;
    }

    public CloudServer getCloudServer() {
        return cloudServer;
    }
}