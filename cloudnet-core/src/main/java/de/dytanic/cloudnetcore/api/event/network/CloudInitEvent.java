/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Calls if the CloudNet instance is successfully started but, the console is not online
 */
public class CloudInitEvent extends AsyncEvent<CloudInitEvent> {

    private CloudNet cloudNet = CloudNet.getInstance();

    public CloudInitEvent() {
        super(new AsyncPosterAdapter<>());
    }

    public CloudNet getCloudNet() {
        return cloudNet;
    }
}
