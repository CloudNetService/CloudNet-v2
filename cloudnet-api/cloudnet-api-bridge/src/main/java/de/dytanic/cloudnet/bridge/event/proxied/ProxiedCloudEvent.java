/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import net.md_5.bungee.api.plugin.Event;

/**
 * This Class is the abstract definition of CloudNet Events for the BungeeCord API
 */
public abstract class ProxiedCloudEvent extends Event {

    /**
     * Returns the CloudAPI instance
     *
     * @return
     */
    public CloudAPI getCloud() {
        return CloudAPI.getInstance();
    }

    /**
     * Returns the CloudProxy instance
     *
     * @return
     */
    public CloudProxy getCloudProxy() {
        return CloudProxy.getInstance();
    }

}
