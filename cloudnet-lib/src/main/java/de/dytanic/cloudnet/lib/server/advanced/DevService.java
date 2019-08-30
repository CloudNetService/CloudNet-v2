/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.advanced;

import java.util.UUID;

/**
 * Created by Tareko on 14.10.2017.
 */
public class DevService {

    private UUID uniqueId;

    private boolean enabled;

    public DevService(UUID uniqueId, boolean enabled) {
        this.uniqueId = uniqueId;
        this.enabled = enabled;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean isEnabled() {
        return enabled;
    }
}