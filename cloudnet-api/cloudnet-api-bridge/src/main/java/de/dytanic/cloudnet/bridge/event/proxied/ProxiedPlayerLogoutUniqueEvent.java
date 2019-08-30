/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import java.util.UUID;

/**
 * Created by Tareko on 07.09.2017.
 */
public class ProxiedPlayerLogoutUniqueEvent extends ProxiedCloudEvent {

    private UUID uniqueId;

    public ProxiedPlayerLogoutUniqueEvent(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}