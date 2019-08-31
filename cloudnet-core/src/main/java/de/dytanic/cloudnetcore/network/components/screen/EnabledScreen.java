/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 25.08.2017.
 */
public class EnabledScreen {

    private ServiceId serviceId;

    private Wrapper wrapper;

    public EnabledScreen(ServiceId serviceId, Wrapper wrapper) {
        this.serviceId = serviceId;
        this.wrapper = wrapper;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}